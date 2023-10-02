package com.example.navigation_test

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Handler
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.LinkedList
import java.util.Queue
import kotlin.Byte
import kotlin.ByteArray
import kotlin.Exception

@SuppressLint("HandlerLeak")
class DataBaseViewModel(userId: String) : ViewModel() {
    private val mHandler: Handler = Handler()
    private val r: Runnable = object : Runnable {
        override fun run() {
            if (job.isNotEmpty()) {
                job.poll()?.let { updateData(it) }

            }
            mHandler.postDelayed(this, 40)
        }
    }

    override fun onCleared() {
        super.onCleared()
        mHandler.removeCallbacks(r)
    }

    init {
        mHandler.postDelayed(r, 1)
        listenData()
    }

    private val _state = MutableLiveData("")
    val state: LiveData<String> = _state

    private val _dataArray = MutableLiveData(ByteArray(10))
    val dataArray: LiveData<ByteArray> = _dataArray

    private val job: Queue<ByteArray> = LinkedList()


    private val firebaseDb = FirebaseFirestore.getInstance()
    private val query =
        firebaseDb.collection("USER").document(userId).collection("Heartbeat_15s")
            .orderBy("timestamp", Query.Direction.DESCENDING).limit(1)


    // 監聽數據並調用 updateUiState 來更新 _uiState
    private fun listenData() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                query.addSnapshotListener { value, e ->
                    val dataList = mutableListOf<ByteArray>() // 創建一個列表來存儲數據
                    if (e != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (value != null) {
                        for (document in value) {
                            val btlist = document.data["heartbeat"] as List<Byte>
                            val data = splitDataArray(btlist)
                            dataList.addAll(data) // 將數據添加到列表中
                            val state = document.data["state"] as String
                            updateState(state)
                            Log.d("chartcheck", document.id)
                        }
                    }
                    // 在處理完所有數據後，一次性將它們添加到隊列
                    for (byteArray in dataList) {
                        addQueue(byteArray)
                    }
                }
            } catch (e: Exception) {
                Log.d(ContentValues.TAG, "Error getting documents: ", e)
            }
        }
    }


    private fun splitDataArray(data: List<Byte>): MutableList<ByteArray> {
        val result = mutableListOf<ByteArray>()
        var count = 0
        var temp: ByteArray? = null
        for (bytes in data) {
            if (count == 0) {
                temp = ByteArray(10)
            }
            temp?.set(count, bytes)
            count++
            if (count == 10) {
                result.add(temp!!)
                count = 0
            }
        }
        return result
    }

    private fun addQueue(data: ByteArray) {
        job.add(data)
    }

    fun updateData(data: ByteArray) {
        _dataArray.postValue(data)
    }

    private fun updateState(state: String) {
        _state.postValue(state)
    }
}

