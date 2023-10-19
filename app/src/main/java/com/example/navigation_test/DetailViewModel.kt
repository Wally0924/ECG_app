package com.example.navigation_test

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DetailViewModel(usrId: String) : ViewModel() {

    private val _arymaCount = MutableLiveData(StateData(0, 0, 0, 0, 0))
    val arymaCount: LiveData<StateData> = _arymaCount

    private val _apneaCount = MutableLiveData(0)
    val apneaCount: LiveData<Int> = _apneaCount

    private val firebaseDb = FirebaseFirestore.getInstance()
    private val queryApnea =
        firebaseDb.collection("USER").document(usrId).collection("apneaRecord")
    private val queryAryma =
        firebaseDb.collection("USER").document(usrId).collection("heartRecord")

    init {
        _apneaCount.value = 0
        _arymaCount.value = StateData(0, 0, 0, 0, 0)
        listenData()
    }

    fun listenData() {
        val currentTime = Timestamp.now()
        val oneHourAgoTimestamp = Timestamp(currentTime.seconds - 3600, currentTime.nanoseconds)

        // 重新初始化計數器
        _apneaCount.value = 0
        _arymaCount.value = StateData(0, 0, 0, 0, 0)

        // 使用 CoroutineScope 來處理異步操作
        CoroutineScope(Dispatchers.IO).launch {
            // 非同步獲取 apnea 資料
            val apneaQuery =
                queryApnea.whereGreaterThanOrEqualTo("timestamp", oneHourAgoTimestamp).get().await()

            // 非同步獲取 aryma 資料
            val arymaQuery =
                queryAryma.whereGreaterThanOrEqualTo("timestamp", oneHourAgoTimestamp).get().await()

            // 在主線程中更新 UI
            withContext(Dispatchers.Main) {
                _apneaCount.value = apneaQuery.size()

                for (document in arymaQuery) {
                    val state = document.data["state"] as? String
                    state?.let {
                        when (state) {
                            "N" -> _arymaCount.value =
                                _arymaCount.value?.copy(nState = _arymaCount.value!!.nState + 1)

                            "S" -> _arymaCount.value =
                                _arymaCount.value?.copy(sState = _arymaCount.value!!.sState + 1)

                            "V" -> _arymaCount.value =
                                _arymaCount.value?.copy(vState = _arymaCount.value!!.vState + 1)

                            "F" -> _arymaCount.value =
                                _arymaCount.value?.copy(fState = _arymaCount.value!!.fState + 1)

                            "Q" -> _arymaCount.value =
                                _arymaCount.value?.copy(qState = _arymaCount.value!!.qState + 1)
                        }
                    }
                }
                Log.d("DetailViewModel", "apneaCount: ${_apneaCount.value}")
                Log.d("DetailViewModel", "apneaCount: ${_arymaCount.value}")
            }
        }
    }


    data class StateData(
        val nState: Int,
        val sState: Int,
        val vState: Int,
        val fState: Int,
        val qState: Int
    )

}