package com.example.navigation_test

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.delay

@Composable
fun ChartView(
    viewModel: ChartViewModel,
    userId: String
) {
    var count by remember { mutableStateOf(0) }
    LaunchedEffect(count) {
        if (count == 100) count = 0
        for (raw in dbViewModel.uiState.value!!) {
            viewModel.drawDiagram(raw, userId)
            delay(50)
            count += 1
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Log.d("checkdata", "ChartView: 有沒有動")
//        val mCanvas = android.graphics.Canvas(viewModel.getBitmap(userId))
//        val paint = android.graphics.Paint()
//        paint.strokeWidth = 2f
//        paint.color = Color.Green.toArgb()
//        var nextX = 0f
//        var nextY = 0f
//        val canvasWidth = size.width
//        val canvasHeight = size.height
//        val maskStart = viewModel.getListData(userId).first
//        var maskEnd = (viewModel.getListData(userId).first + (data.size * speed))
//
//        if (maskEnd < canvasWidth) {
//            mask.setBounds(maskStart.toInt(), 0, maskEnd.toInt(), canvasHeight.toInt())
//            mask.draw(mCanvas)
//        } else {
//            mask.setBounds(maskStart.toInt(), 0, canvasWidth.toInt(), canvasHeight.toInt())
//            mask.draw(mCanvas)
//            maskEnd -= canvasWidth
//            mask.setBounds(0, 0, maskEnd.toInt(), canvasHeight.toInt())
//            mask.draw(mCanvas)
//        }
//        mCanvas.drawLine(
//            maskEnd + 1f,
//            0f,
//            maskEnd + 1f,
//            canvasHeight,
//            paint
//        )
//        for (rawDatum in data) {
//            nextX = viewModel.getListData(userId).first + speed
//            if (nextX >= canvasWidth) {
//                nextX -= canvasWidth
//            } else {
//                nextY = canvasHeight - (((rawDatum.toInt() + 30) and 0xFF) * proportion)
//                mCanvas.drawLine(
//                    viewModel.getListData(userId).first,
//                    viewModel.getListData(userId).second,
//                    nextX,
//                    nextY,
//                    paint
//                )
//            }
//            viewModel.updateData(userId, Pair(nextX, nextY))
//        }
        drawImage(viewModel.getBitmap(userId).asImageBitmap())
    }
}

//private fun generateRandomData(): ByteArray {
//    return ByteArray(5) {
//        (50..200).random().toByte() // 隨機生成測試數據
//    }
//}