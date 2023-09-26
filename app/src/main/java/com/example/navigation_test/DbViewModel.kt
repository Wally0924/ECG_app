package com.example.navigation_test

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class DataBaseViewModel : ViewModel() {
    private val _uiState = MutableLiveData(mutableListOf<ByteArray>())
    val uiState: LiveData<MutableList<ByteArray>> = _uiState

    private val _dataArray = MutableLiveData(ByteArray(5))
    val dataArray: LiveData<ByteArray> = _dataArray

    private val firebaseDb = FirebaseFirestore.getInstance()
    private val query =
        firebaseDb.collection("USER").document("7pB7dTaNOshzm0OoQCtk").collection("Heartbeat_15s")
            .orderBy("timestamp").limit(1)


    private fun updateUiState(newData: MutableList<ByteArray>) {
        _uiState.value = newData
    }

    // 監聽數據並調用 updateUiState 來更新 _uiState
    fun listenData() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = withContext(Dispatchers.IO) { query.get().await() }
                for (document in result) {
                    val btlist = document.data["heartbeat"] as List<Byte>
                    val data = splitDataArray(btlist)
                    updateUiState(data)
                }
                delay(10)
            } catch (e: Exception) {
                Log.d(ContentValues.TAG, "Error getting documents: ", e)
            }
        }
    }


    private fun splitDataArray(data: List<Byte>): MutableList<ByteArray> {
        val result = mutableListOf<ByteArray>()
        var count = 0
        val temp = ByteArray(5)
        for (bytes in data) {
            temp[count] = bytes
            count++
            if (count == 5) {
                result.add(temp)
                count = 0
            }
        }
        return result
    }

}

