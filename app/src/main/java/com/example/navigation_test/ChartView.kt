package com.example.navigation_test

import android.graphics.Bitmap
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay


@Composable
fun ChartView(
    viewModel: ChartViewModel
) {
    Log.d("ChartView", "ChartView重繪")
    var data by remember { mutableStateOf(generateRandomData()) }
    LaunchedEffect(true) {
        while (true) {
            data = generateRandomData()
            delay(100)
        }
    }
    val mBitmap =
        Bitmap.createBitmap(3000, 2500, Bitmap.Config.ARGB_8888)
    val mCanvas = android.graphics.Canvas(mBitmap)
    val Mask = ShapeDrawable(RectShape())
    val proportion = 1.8f
    val speed = 20f

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val paint = android.graphics.Paint()
        paint.color = Color.Green.toArgb()
        var nextX = 0f
        var nextY = 0f
        val canvasWidth = size.width
        val canvasHeight = size.height
        val maskStart = viewModel.getmLastX()
        var maskEnd = (viewModel.getmLastX() + (data.size * speed))
        if (maskEnd < canvasWidth) {
            Mask.setBounds(maskStart.toInt(), 0, maskEnd.toInt(), canvasHeight.toInt())
            Mask.draw(mCanvas)
        } else {
            Mask.setBounds(maskStart.toInt(), 0, canvasWidth.toInt(), canvasHeight.toInt())
            Mask.draw(mCanvas)
            maskEnd -= canvasWidth
            Mask.setBounds(0, 0, maskEnd.toInt(), canvasHeight.toInt())
            Mask.draw(mCanvas)
        }
        mCanvas.drawLine(
            maskEnd + 1f,
            0f,
            maskEnd + 1f,
            canvasHeight,
            paint
        )
        for (rawDatum in data) {
            nextX = viewModel.getmLastX() + speed
            if (nextX >= canvasWidth) {
                nextX -= canvasWidth
            } else {
                nextY = canvasHeight - ((rawDatum.toInt() and 0xFF) * proportion)
                mCanvas.drawLine(
                    viewModel.getmLastX(),
                    viewModel.getmLastY(),
                    nextX,
                    nextY,
                    paint
                )
            }
            viewModel.updateLastXY(nextX, nextY)
        }
        drawImage(mBitmap.asImageBitmap())
    }
}

private fun generateRandomData(): ByteArray {
    return ByteArray(3) {
        (50..200).random().toByte() // 隨機生成測試數據
    }
}