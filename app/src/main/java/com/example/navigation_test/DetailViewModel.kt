package com.example.navigation_test

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DetailViewModel(usrId: String) : ViewModel() {
    private var _sevenDaysData = MutableLiveData(mapOf<String, List<StateData>>())
    val sevenDaysData: LiveData<Map<String, List<StateData>>> = _sevenDaysData
    private var _sevenDaysApnea = MutableLiveData(mapOf<String, List<Int>>())
    val sevenDaysApnea: LiveData<Map<String, List<Int>>> = _sevenDaysApnea

    var apneaRecordSum = 0
    private val _dataComplete = MutableLiveData(false)
    val dataComplete: LiveData<Boolean> = _dataComplete

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
        apneaRecordSum = 0
        listenData()
        historyListenData()
    }

    private fun listenData() {
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
                for (document in apneaQuery) {
                    val state = document.data["state"] as Number
                    //算正常的次數
                    if (state.toInt() == 0) {
                        _apneaCount.value = _apneaCount.value?.plus(1)
                    }
                }
                apneaRecordSum = apneaQuery.size()
                for (document in arymaQuery) {
                    val state = document.data["state"] as? String
                    state?.let {
                        when (state) {
                            "Normal" -> _arymaCount.value =
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
            }
            _dataComplete.postValue(true)
        }
    }

    private fun historyListenData() {
        val currentTime = Timestamp.now()
        val sevenDayAgoTime = sevenDayAgo()
        CoroutineScope(Dispatchers.IO).launch {
            val arymaQuery = queryAryma
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .whereLessThanOrEqualTo("timestamp", currentTime)
                .whereGreaterThanOrEqualTo("timestamp", sevenDayAgoTime)
            val apneaQuery = queryApnea
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .whereLessThanOrEqualTo("timestamp", currentTime)
                .whereGreaterThanOrEqualTo("timestamp", sevenDayAgoTime)
            val querySnapshot = arymaQuery.get().await()
            val querySnapshotApnea = apneaQuery.get().await()
            Log.d("apneaQuery", "${querySnapshot.size()}")
            Log.d("apneaQuery", "${querySnapshotApnea.size()}")
            val newSevenDaysData = buildNewSevenDaysData(querySnapshot)
            val newSevenDaysApnea = buildApneaSevenDaysData(querySnapshotApnea)

            withContext(Dispatchers.Main) {
                _sevenDaysData.value = newSevenDaysData
                _sevenDaysApnea.value = newSevenDaysApnea
            }

        }
    }

    private fun buildNewSevenDaysData(querySnapshot: QuerySnapshot): Map<String, List<StateData>> {
        val newSevenDaysData = mutableMapOf<String, List<StateData>>()

        for (document in querySnapshot.documents) {
            val dataTimestamp = document.getTimestamp("timestamp")
            val state = document.getString("state")

            if (dataTimestamp != null && state != null) {
                val dateKey = dateFormat(dataTimestamp.toDate())
                val hourOfDay = calculateHourOfDay(dataTimestamp.seconds * 1000)
                newSevenDaysData.getOrPut(dateKey) { MutableList(24) { StateData(0, 0, 0, 0, 0) } }
                when (state) {
                    "Normal" -> newSevenDaysData[dateKey]?.get(hourOfDay)?.nState =
                        newSevenDaysData[dateKey]?.get(hourOfDay)?.nState?.plus(1) ?: 0

                    "S" -> newSevenDaysData[dateKey]?.get(hourOfDay)?.sState =
                        newSevenDaysData[dateKey]?.get(hourOfDay)?.sState?.plus(1) ?: 0

                    "V" -> newSevenDaysData[dateKey]?.get(hourOfDay)?.vState =
                        newSevenDaysData[dateKey]?.get(hourOfDay)?.vState?.plus(1) ?: 0

                    "F" -> newSevenDaysData[dateKey]?.get(hourOfDay)?.fState =
                        newSevenDaysData[dateKey]?.get(hourOfDay)?.fState?.plus(1) ?: 0

                    "Q" -> newSevenDaysData[dateKey]?.get(hourOfDay)?.qState =
                        newSevenDaysData[dateKey]?.get(hourOfDay)?.qState?.plus(1) ?: 0
                }
            }
        }
        return newSevenDaysData
    }

    private fun buildApneaSevenDaysData(querySnapshot: QuerySnapshot): Map<String, List<Int>> {
        val newSevenDaysData = mutableMapOf<String, List<Int>>()

        for (document in querySnapshot.documents) {
            val dataTimestamp = document.getTimestamp("timestamp")
            val state = document.data?.get("state") as Number

            if (dataTimestamp != null && state.toInt() == 1) {
                val dateKey = dateFormat(dataTimestamp.toDate())
                val hourOfDay = calculateHourOfDay(dataTimestamp.seconds * 1000)

                // 取得對應日期的列表，若不存在則創建一個
                val dataList = newSevenDaysData.getOrPut(dateKey) { MutableList(24) { 0 } }.toMutableList()
                dataList[hourOfDay] = dataList[hourOfDay] + 1
                Log.d("apneaQuery", "$dataList")
                newSevenDaysData[dateKey] = dataList
            }
        }
        return newSevenDaysData
    }


    @SuppressLint("SimpleDateFormat")
    fun calculateHourOfDay(timestamp: Long): Int {
        val format = SimpleDateFormat("HH")
        format.timeZone = TimeZone.getTimeZone("GMT+8")
        val date = Date(timestamp)
        return format.format(date).toInt()
    }


    private fun sevenDayAgo(): Timestamp {
        // 創建一個 Calendar 實例
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))

        // 計算並設定 7 天前的日期
        calendar.add(Calendar.DAY_OF_MONTH, -6)

        // 將日期設定為當天的0時0分0秒
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // 創建一個 Timestamp 物件，表示 7 天前的日期的0時
        return Timestamp((calendar.time))
    }


    data class StateData(
        var nState: Int,
        var sState: Int,
        var vState: Int,
        var fState: Int,
        var qState: Int
    )

    data class RecordData(
        val state: String,
        val hour: Int
    )


}