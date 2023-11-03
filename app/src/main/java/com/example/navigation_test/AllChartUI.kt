package com.example.navigation_test

import android.graphics.Typeface
import android.text.TextUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.yml.charts.axis.AxisData
import co.yml.charts.axis.DataCategoryOptions
import co.yml.charts.common.model.PlotType
import co.yml.charts.common.model.Point
import co.yml.charts.common.utils.DataUtils
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.StackedBarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarPlotData
import co.yml.charts.ui.barchart.models.BarStyle
import co.yml.charts.ui.barchart.models.GroupBar
import co.yml.charts.ui.barchart.models.GroupBarChartData
import co.yml.charts.ui.barchart.models.SelectionHighlightData
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import kotlin.random.Random

@Composable
fun HourArymaCountChart(arymaCount: DetailViewModel.StateData) {
    val xLabel = listOf("N", "S", "V", "F", "Q")
    val barData = listOf(
        getBarData(arymaCount.nState, 0, Color(0xFF28FF28)),
        getBarData(arymaCount.sState, 1, Color(0xFFF9F900)),
        getBarData(arymaCount.vState, 2, Color(0xFFFFAA00)),
        getBarData(arymaCount.fState, 3, Color(0xFFFF6600)),
        getBarData(arymaCount.qState, 4, Color(0xFFFF0000))
    )
    val yStepSize = 10
    val xAxisData = AxisData.Builder()
        .axisStepSize(35.dp)
        .steps(barData.size - 1)
        .bottomPadding(12.dp)
        .axisLabelAngle(15f)
        .startDrawPadding(50.dp)
        .shouldDrawAxisLineTillEnd(true)
        .labelData { index -> xLabel[index] }
        .typeFace(Typeface.defaultFromStyle(Typeface.BOLD))
        .axisLabelFontSize(20.sp)
        .build()
    val yAxisData = AxisData.Builder()
        .steps(yStepSize)
        .labelAndAxisLinePadding(20.dp)
        .axisOffset(20.dp)
//        .labelData { index -> (index * (maxRange / yStepSize)).toString() }
        .build()
    val barChartData = BarChartData(
        chartData = barData,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        barStyle = BarStyle(
            paddingBetweenBars = 20.dp,
            barWidth = 25.dp
        ),
        showYAxis = false,
        showXAxis = true,
        horizontalExtraSpace = 10.dp,
    )
    BarChart(
        modifier = Modifier
            .height(200.dp)
            .width(290.dp), barChartData = barChartData
    )
}

fun getBarData(value: Int, index: Int, color: Color): BarData {
    val point = Point(
        index.toFloat(),
        value.toFloat()
    )
    return BarData(
        point = point,
        color = color,
        dataCategoryOptions = DataCategoryOptions(),
        label = ""
    )
}

@Composable
fun HourApneaCountChart(apneaCount: Int, sum: Int): Float {
    val nmlPercentage = if (apneaCount == 0) 0f else apneaCount.toFloat() / sum.toFloat()
    val inmlPercentage = if (nmlPercentage == 0f) 1f else 1.toFloat() - nmlPercentage

//    val nmlPercentage = 0.8f
//    val inmlPercentage = 1 - nmlPercentage

    val pieChartData = PieChartData(
        slices = listOf(
            PieChartData.Slice("正常", nmlPercentage, Color(0xFF00FF0D)),
            PieChartData.Slice("不正常", inmlPercentage, Color(0xFFFF0000)),
        ),
        plotType = PlotType.Pie
    )

    val pieChartConfig =
        PieChartConfig(
            activeSliceAlpha = .9f,
            isEllipsizeEnabled = true,
            sliceLabelEllipsizeAt = TextUtils.TruncateAt.MIDDLE,
            sliceLabelTypeface = Typeface.defaultFromStyle(Typeface.ITALIC),
            isAnimationEnable = true,
            chartPadding = 20,
            showSliceLabels = false,
            labelVisible = false,
            animationDuration = 1200
        )
    PieChart(
        modifier = Modifier
            .width(200.dp)
            .height(200.dp)
            .padding(start = 15.dp),
        pieChartData,
        pieChartConfig
    ) {
        // on slice click
    }

    return nmlPercentage
}

@Composable
fun VerticalStackedBarChart(
    day: String,
    armyHistoryData: Map<String, List<DetailViewModel.StateData>>
) {
    if (!armyHistoryData.containsKey(day)) {
        return
    } else {
        val listSize = 24
        val groupBarData = getGroupBarData(armyHistoryData[day] ?: listOf())
        val xAxisData = AxisData.Builder()
            .labelAndAxisLinePadding(10.dp)
            .axisStepSize(60.dp)
            .steps(listSize - 1)
            .labelData { index -> "${index}時" }
            .shouldDrawAxisLineTillEnd(true)
            .startDrawPadding(60.dp)
            .bottomPadding(0.dp)
            .typeFace(Typeface.defaultFromStyle(Typeface.BOLD))
            .build()
        val colorPaletteList = listOf(
            Color(0xFF28FF28),
            Color(0xFFF9F900),
            Color(0xFFFFAA00),
            Color(0xFFFF6600),
            Color(0xFFFF0000)
        )
        val groupBarPlotData = BarPlotData(
            groupBarList = groupBarData,
            barStyle = BarStyle(
                barWidth = 20.dp,
                selectionHighlightData = SelectionHighlightData(
                    isHighlightFullBar = true,
                    groupBarPopUpLabel = { _, value ->
                        "Value : ${String.format("%.2f", value)}"
                    }
                ),
                paddingBetweenBars = 45.dp,
                cornerRadius = 20.dp
            ),
            barColorPaletteList = colorPaletteList
        )
        val groupBarChartData = GroupBarChartData(
            barPlotData = groupBarPlotData,
            xAxisData = xAxisData,
            paddingEnd = 0.dp,
            drawBar = { drawScope, barChartData, barStyle, drawOffset, height, barIndex ->
                with(drawScope) {
                    drawRect(
                        color = colorPaletteList[barIndex],
                        topLeft = drawOffset,
                        size = Size(barStyle.barWidth.toPx(), height),
                        style = barStyle.barDrawStyle,
                        blendMode = barStyle.barBlendMode
                    )
                }
            }
        )
        StackedBarChart(
            modifier = Modifier
                .height(165.dp)
                .width(800.dp),
            groupBarChartData = groupBarChartData
        )
    }
}

fun getGroupBarData(dailyData: List<DetailViewModel.StateData>): List<GroupBar> {
    val list = mutableListOf<GroupBar>()
    for (index in 0 until 24) {
        val barList = mutableListOf<BarData>()
        for (i in 0 until 5) {
            val barValue = dailyData[index].let {
                when (i) {
                    0 -> it.nState.toFloat()
                    1 -> it.sState.toFloat()
                    2 -> it.vState.toFloat()
                    3 -> it.fState.toFloat()
                    4 -> it.qState.toFloat()
                    else -> 0f
                }
            }
            barList.add(
                BarData(
                    Point(
                        index.toFloat(),
                        barValue
                    ),
                    label = "B$i",
                    description = "Bar at $index with label B$i has value ${
                        String.format(
                            "%.2f", barValue
                        )
                    }"
                )
            )
        }
        list.add(GroupBar(index.toString(), barList))
    }
    return list
}

@Composable
fun ChartLabelText(labelText: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .height(20.dp)
                .width(20.dp)
                .background(color = color)
        )
        Spacer(modifier = Modifier.padding(start = 20.dp))
        Text(
            text = labelText,
            fontSize = 22.sp,
            color = Color.Black
        )
    }
}

@Composable
fun HistoryChartLabelText(labelText: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxHeight()
    ) {
        Spacer(modifier = Modifier.padding(start = 25.dp))
        Box(
            modifier = Modifier
                .size(18.dp)
                .padding(top = 5.dp)
                .background(color = color)
        )
        Spacer(modifier = Modifier.padding(start = 10.dp))
        Text(
            text = labelText,
            fontSize = 16.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.padding(start = 25.dp))
    }
}

data class TabItem(
    val title: String,
    val unselectIcon: ImageVector,
    val selectedIcon: ImageVector
)
