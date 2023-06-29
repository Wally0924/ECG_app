package com.example.navigation_test

import android.content.ContentValues
import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun EcgPlot(ecgData:List<Float>, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val chart = remember {
        LineChart(context)
    }
    val lineDataSet = LineDataSet(ecgData.toEntries(), "心電圖")
    lineDataSet.setDrawCircles(false)
    lineDataSet.setDrawCircleHole(false)
    lineDataSet.color = Color.GREEN
    lineDataSet.lineWidth = 2f
    // 圖表敘述
    val description = chart.description
    description.isEnabled = false
    //圖表圖示
    val legend = chart.legend
    legend.isEnabled = false
    //X軸
    val xAxis = chart.xAxis
    xAxis.setDrawLabels(false)
    xAxis.setDrawGridLines(false)
    xAxis.setDrawAxisLine(false)
    //Y軸
    val yAxisRight = chart.axisRight
    val yAxisLeft = chart.axisLeft
    yAxisRight.setDrawLabels(false)
    yAxisRight.setDrawGridLines(false)
    yAxisRight.setAxisMaxValue(10F)
    yAxisRight.setAxisMinValue(-10F)
    yAxisRight.setDrawAxisLine(false)
    yAxisLeft.setDrawLabels(false)
    yAxisLeft.setDrawGridLines(false)
    yAxisLeft.setAxisMaxValue(10F)
    yAxisLeft.setAxisMinValue(-10F)
    yAxisLeft.setDrawAxisLine(false)


    AndroidView(
        { chart },
        modifier = modifier.fillMaxWidth()
    ) { lineChart ->
        with(lineChart) {
            isDoubleTapToZoomEnabled = false
            isDragEnabled = false
            setDrawBorders(false)
            setTouchEnabled(false)
            setScaleEnabled(false)
            setDrawGridBackground(false)
            setBackgroundColor(Color.TRANSPARENT)
            setTouchEnabled(false)
            data = LineData(lineDataSet)
            setVisibleXRangeMaximum(1000F)
            lineChart.animateX(9000, Easing.Linear)
            centerViewToAnimated(
                lineData.entryCount.toFloat(),
                0F,
                YAxis.AxisDependency.RIGHT,
                30000
            )
        }
    }
}

fun List<Float>.toEntries(): List<Entry> = mapIndexed { index, value ->
    Entry(index.toFloat(), value)
}

