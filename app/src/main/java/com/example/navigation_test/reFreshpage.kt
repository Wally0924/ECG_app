package com.example.navigation_test

import android.content.ContentValues
import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

fun reFreshHomepage(
    mbViewModel: MemberViewModel,
    onComplete: () -> Unit
) {
    var usrList = mutableListOf<String>()
    val firebaseDb = FirebaseFirestore.getInstance()
    val query = firebaseDb.collection("USER")

    CoroutineScope(Dispatchers.Main).launch {
        try {
            val result = withContext(Dispatchers.IO) { query.get().await() }
            for (document in result) {
                val name = document.data["userName"]
                val userID = document.id
                mbViewModel.updateData(userID, name.toString())
                usrList.add(userID)
                Log.d("mbViewModel", mbViewModel.getListData(userID))
            }
//
//            val fetchTasks = mutableListOf<Deferred<Unit>>()
//            usrList.forEach { usrId ->
//                val fetchTask = async(Dispatchers.IO) {
//                    val ecgDataResult = withContext(Dispatchers.IO) {
//                        query.document(usrId).collection("Heartbeat_15s").get().await()
//                    }
//
//                    val egDtFloat = ecgDataResult.documents.mapNotNull { ecgData ->
//                        val list = ecgData.data?.get("heartbeat") as? List<Double>
//                        list?.map { it.toFloat() }
//                    }.flatten()
//
//                    egDtList[usrId] = egDtFloat
//                    println(egDtFloat.size)
//                }
//                fetchTasks.add(fetchTask)
//            }
//
//            fetchTasks.awaitAll() // 等待所有非同步操作完成
//            mblist.clear()
            userId.clear()
//            ecgDataList.clear()
//            mblist.addAll(mbbList)
            userId.addAll(usrList)
//            for (usr in usrList) {
//                egDtList[usr]?.let { ecgDataList.add(it) }
//            }
//            chartViewModel.initKey(userId)
            onComplete()
            delay(1000)
        } catch (e: Exception) {
            Log.d(ContentValues.TAG, "Error getting documents: ", e)
        }
    }
}

@Composable
fun Waiting() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lotie))
    val isPlaying by remember { mutableStateOf(true) }
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        iterations = LottieConstants.IterateForever
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
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
                fontSize = 26.sp,
                color = Color.Black
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}



