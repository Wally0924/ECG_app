package com.example.navigation_test

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//主頁右側Lazy圖表
@Composable
fun ChartList(data: MutableList<String>, chartViewModel: ChartViewModel) {
    LazyVerticalGrid(columns = GridCells.Adaptive(450.dp)) {
        items(data) { item ->
            ChartItem(item, chartViewModel)
        }
    }
}

@Composable
fun ChartItem(usrId: String, chartViewModel: ChartViewModel) {
    val dbViewModel = remember(usrId) { DataBaseViewModel(usrId) }
    val state by dbViewModel.state.observeAsState(initial = "")
    Box(
        modifier = Modifier
            .padding(8.dp)
            .aspectRatio(1.5f)
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = 2.dp, color = Color.Blue, shape = RoundedCornerShape(10.dp)
            ), contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = mbViewModel.getListData(usrId), fontSize = 40.sp)
                Text(text = "目前狀態: $state", fontSize = 25.sp)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ChartView(chartViewModel, dbViewModel, usrId)
            }
        }
    }
}

@Composable
fun ChartView(
    viewModel: ChartViewModel,
    dbViewModel: DataBaseViewModel,
    userId: String
) {
    val data by dbViewModel.dataArray.observeAsState(initial = ByteArray(10))
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val proportion = 2.0f
        val speed = 0.5f
        val mask = ShapeDrawable(RectShape())
        val mCanvas = android.graphics.Canvas(viewModel.getBitmap(userId))
        val paint = android.graphics.Paint()
        paint.strokeWidth = 1f
        paint.color = Color.Green.toArgb()
        paint.flags = android.graphics.Paint.ANTI_ALIAS_FLAG
        paint.style = android.graphics.Paint.Style.FILL_AND_STROKE
        paint.textSize = 50f
        paint.strokeJoin = android.graphics.Paint.Join.ROUND
        var nextX = 0f
        var nextY = 0f
        val canvasWidth = size.width
        val canvasHeight = size.height
        val maskStart = viewModel.getListData(userId).first
        var maskEnd = (viewModel.getListData(userId).first + 10 * speed)

        if (maskEnd < canvasWidth) {
            mask.setBounds(maskStart.toInt(), 0, maskEnd.toInt(), canvasHeight.toInt())
            mask.draw(mCanvas)
        } else {
            mask.setBounds(maskStart.toInt(), 0, canvasWidth.toInt(), canvasHeight.toInt())
            mask.draw(mCanvas)
            maskEnd -= canvasWidth
            mask.setBounds(0, 0, maskEnd.toInt(), canvasHeight.toInt())
            mask.draw(mCanvas)
        }
        mCanvas.drawLine(
            maskEnd + 1f,
            0f,
            maskEnd + 1f,
            canvasHeight,
            paint
        )
        for (rawDatum in data) {
            nextX = viewModel.getListData(userId).first + speed
            if (nextX >= canvasWidth) {
                nextX -= canvasWidth
            } else {
                nextY = canvasHeight - (((rawDatum.toInt() and 0xFF) - 65) * proportion)
                mCanvas.drawLine(
                    viewModel.getListData(userId).first,
                    viewModel.getListData(userId).second,
                    nextX,
                    nextY,
                    paint
                )
            }
            viewModel.updateData(userId, Pair(nextX, nextY))
        }
        drawImage(viewModel.getBitmap(userId).asImageBitmap())
    }
}

//private fun generateRandomData(): ByteArray {
//    return ByteArray(5) {
//        (50..200).random().toByte() // 隨機生成測試數據
//    }
//}