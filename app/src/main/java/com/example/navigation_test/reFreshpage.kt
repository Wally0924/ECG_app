package com.example.navigation_test

import android.content.ContentValues
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


fun reFreshHomepage(navController: NavController) {
    var mbbList = mutableListOf<String>()
    var usrList = mutableListOf<String>()
    val firebaseDb = FirebaseFirestore.getInstance()
    val egDtList = mutableMapOf<String,List<Float>>()
    val query = firebaseDb.collection("USER")

    CoroutineScope(Dispatchers.Main).launch {
        try {
            val result = withContext(Dispatchers.IO) { query.orderBy("userEmail").get().await() }
            for (document in result) {
                val name = document.data["userName"]
                val userID = document.id
                mbbList.add(name.toString())
                usrList.add(userID.toString())
            }

            val fetchTasks = mutableListOf<Deferred<Unit>>()
            for (usrId in usrList) {
                print(usrId)
                val fetchTask = async(Dispatchers.IO) {
                    val egDtFloat = mutableListOf<Float>()
                    val ecgDataResult = withContext(Dispatchers.IO) { query.document(usrId).collection("Ecg_Data").get().await() }
                    for (ecgData in ecgDataResult) {
                        val list = ecgData.data["ecgData"] as? List<Double>
                        if (list != null) {
                            egDtFloat.clear()
                            for (i in 1..10) {
                                egDtFloat.addAll(list.map { it.toFloat() })
                            }
                        } else {
                            egDtFloat.clear()
                            egDtFloat.add(0f)
                        }
                    }
                    egDtList[usrId] = egDtFloat.toList()
                    println(egDtList[usrId])
                }
                fetchTasks.add(fetchTask)
            }

            fetchTasks.awaitAll() // 等待所有非同步操作完成

            mblist.clear()
            userId.clear()
            ecgDataList.clear()
            mblist.addAll(mbbList)
            userId.addAll(usrList)
            for(usr in usrList){
                egDtList[usr]?.let { ecgDataList.add(it) }
            }
            for (item in ecgDataList) {
                println("檢查答案: ${item[0]}")
            }
            navController.navigate("home/ ")
        } catch (e: Exception) {
            Log.d(ContentValues.TAG, "Error getting documents: ", e)
        }
    }
}

@Composable
fun Waiting(navController: NavHostController) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lotie))
    var isPlaying by remember { mutableStateOf(true) }
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        iterations = LottieConstants.IterateForever
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            modifier = Modifier.size(400.dp),
            composition = composition,
            progress = progress
        )
        Text(
            text = "資料同步中...",
            style = TextStyle(
                fontSize = 25.sp,
                color = Color.Black
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}



