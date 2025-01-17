package com.example.navigation_test

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
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
class DataBaseViewModel(usrId: String) : ViewModel() {
    private val mHandler: Handler = Handler()
    private val timeHandler: Handler = Handler()
    private val r: Runnable = object : Runnable {
        override fun run() {
            if (job.isNotEmpty()) {
                job.poll()?.let { updateData(it) }
            }
            mHandler.postDelayed(this, 37)
        }
    }

    // 定義一個計時器任務，當計時器達到指定時間時更改 state
    private val timerRunnable = Runnable {
        updateState("尚未連線") // 更改 state 為 "尚未連線"
        updateApneaState("尚未連線")// 更改 apneaState 為 "尚未連線"
        chartViewModel.initUsrIdData(usrId) //刷新圖資訊
        job.clear()  // 清空隊列
        updateData(ByteArray(10)) // 更新數據為空
    }

    // 重置計時器
    private fun resetTimer() {
        timeHandler.removeCallbacks(timerRunnable) // 移除之前的計時器任務
        timeHandler.postDelayed(timerRunnable, 6500) // 設定計時器為 6.5 秒
    }

    override fun onCleared() {
        super.onCleared()
        mHandler.removeCallbacks(r)
    }

    init {
        mHandler.postDelayed(r, 0)
        listenData()
    }

    private val _state = MutableLiveData("尚未連線")
    val state: LiveData<String> = _state

    private val _apneaState = MutableLiveData("尚未連線")
    val apneaState: LiveData<String> = _apneaState

    private val _dataArray = MutableLiveData(ByteArray(10))
    val dataArray: LiveData<ByteArray> = _dataArray

    private val job: Queue<ByteArray> = LinkedList()


    private val firebaseDb = FirebaseFirestore.getInstance()
    private val query =
        firebaseDb.collection("USER").document(usrId).collection("Heartbeat_15s")
            .orderBy("timestamp", Query.Direction.DESCENDING).limit(1)
    private val Apneaquery =
        firebaseDb.collection("USER").document(usrId).collection("apneaRecord")
            .orderBy("timestamp", Query.Direction.DESCENDING).limit(1)

    private var isFirstDataFetch = true // 新增一個布爾變數，用於追蹤是否是首次讀取資料
    private var isFirstDataApnea = true

    private fun listenData() {
        CoroutineScope(Dispatchers.Main).launch {
            query.addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (value != null) {
                    if (!isFirstDataFetch) {
                        Log.d("firsttime", "開始讀取資料")
                        if (_apneaState.value == "尚未連線") {
                            updateApneaState("測量中")
                        }
                        val dataList = mutableListOf<ByteArray>() // 創建一個列表來存儲數據
                        for (document in value) {
                            val btlist = document.data["heartbeat"] as List<Byte>
                            val data = splitDataArray(btlist)
                            dataList.addAll(data) // 將數據添加到列表中
                            val state = document.data["state"] as? String
                            if (state != null) {
                                updateState(state)
                            }
                            resetTimer() // 重置計時器
                        }
                        // 在處理完所有數據後，一次性將它們添加到隊列
                        for (byteArray in dataList) {
                            addQueue(byteArray)
                        }
                    } else {
                        if(isFirstDataFetch)
                            Log.d("firsttime", "首次讀取資料")

                        isFirstDataFetch = false // 將首次讀取標誌設為 false，以後的資料都會被處理

                        if(!isFirstDataFetch)
                            Log.d("firsttime", "非首次讀取資料")
                    }
                }
            }

            Apneaquery.addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (value != null) {
                    if (!isFirstDataApnea) {
                        for (document in value) {
                            val state = document.data["state"] as Number
                            if (state.toInt() == 0) {
                                updateApneaState("正常")
                            } else {
                                updateApneaState("異常")
                            }
                        }
                    } else {
                        isFirstDataApnea = false
                    }
                }
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

    private fun updateApneaState(state: String) {
        _apneaState.postValue(state)
    }

}

