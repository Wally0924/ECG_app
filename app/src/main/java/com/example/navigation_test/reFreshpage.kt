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
import kotlinx.coroutines.Dispatchers
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
            }
            userId.clear()
            userId.addAll(usrList)
            onComplete()
            delay(1000)
        } catch (e: Exception) {
            Log.d(ContentValues.TAG, "Error getting documents: ", e)
        }
    }
}




