package com.example.navigation_test

import android.graphics.Typeface
import android.text.TextUtils
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import co.yml.charts.axis.AxisData
import co.yml.charts.axis.DataCategoryOptions
import co.yml.charts.common.extensions.isNotNull
import co.yml.charts.common.model.PlotType
import co.yml.charts.common.model.Point
import co.yml.charts.common.utils.DataUtils
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarChartType
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarStyle
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import kotlin.random.Random

@Composable
fun DetailPage(
    usrId: String,
    dbViewModel: DataBaseViewModel,
    chartViewModel: ChartViewModel,
    state: String,
    onDismissRequest: () -> Unit
) {
    val detailViewModel = remember(usrId) { DetailViewModel(usrId) }
    val apneaCount by detailViewModel.apneaCount.observeAsState()
    val arymaCount by detailViewModel.arymaCount.observeAsState()
    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .height(700.dp)
                .width(1000.dp)
                .background(
                    Color.White, RoundedCornerShape(10.dp)
                ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, start = 30.dp, bottom = 10.dp, end = 30.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = mbViewModel.getListData(usrId),
                    fontSize = 48.sp,
                    color = Color.Black
                )
                FilledTonalButton(
                    onClick = { onDismissRequest() },
                    elevation = ButtonDefaults.filledTonalButtonElevation(5.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(Color.LightGray)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "back",
                        tint = Color.Black,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp, end = 5.dp)
                    )
                    Text(text = "返回", fontSize = 28.sp, color = Color.Black)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .padding(20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(3f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    ChartView(chartViewModel, dbViewModel, usrId)
                }
                Column(
                    modifier = Modifier
                        .weight(2f)
                        .padding(start = 20.dp)
                ) {
                    //圖旁右上Card
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .padding(bottom = 10.dp)
                    ) {
                        Text(
                            text = "心律辨識狀態(5秒/次): ",
                            fontSize = 22.sp,
                            modifier = Modifier.padding(top = 10.dp, start = 20.dp),
                            color = Color.Black
                        )
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {

                            Text(
                                text = state,
                                fontSize = 30.sp,
                                modifier = Modifier.padding(
                                    top = 10.dp,
                                    start = 20.dp,
                                    bottom = 10.dp,
                                    end = 20.dp,
                                ),
                                color = if(state == "Normal") Color.Green else if (state == "尚未連線") Color.Black else Color.Red
                            )
                        }
                    }
                    //圖旁右下Card
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .padding(top = 10.dp)
                    ) {
                        Text(
                            text = "睡眠呼吸中止辨識狀態(分/次): ",
                            fontSize = 22.sp,
                            modifier = Modifier.padding(top = 10.dp, start = 20.dp),
                            color = Color.Black
                        )
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "良好",
                                fontSize = 30.sp,
                                modifier = Modifier.padding(
                                    top = 10.dp,
                                    start = 20.dp,
                                    bottom = 10.dp,
                                    end = 20.dp,
                                ),
                                color = Color.Black
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //圖下左Card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White,
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .padding(end = 15.dp)
                ) {
                    Text(
                        text = "過去一小時內發生心律不整次數: ",
                        fontSize = 22.sp,
                        modifier = Modifier.padding(top = 10.dp, start = 20.dp),
                        color = Color.Black
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        arymaCount?.let { HourArymaCountChart(it) }
//                        arymaCount?.let { HourArymaCountChart(it) }
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 10.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            ChartLabelText(
                                labelText = "N: ${arymaCount?.nState}次",
                                color = Color(0xFF002B5B)
                            )
                            ChartLabelText(
                                labelText = "S: ${arymaCount?.sState}次",
                                color = Color(0xFF2B4865)
                            )
                            ChartLabelText(
                                labelText = "V: ${arymaCount?.vState}次",
                                color = Color(0xFF256D85)
                            )
                            ChartLabelText(
                                labelText = "F: ${arymaCount?.fState}次",
                                color = Color(0xFF806D85)
                            )
                            ChartLabelText(
                                labelText = "Q: ${arymaCount?.qState}次",
                                color = Color(0xFFB2B8FF)
                            )
                        }
                    }
                }
                //圖下右Card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White,
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .padding(start = 15.dp)
                ) {
                    Text(
                        text = "過去一小時內發生呼吸睡眠中止比例: ",
                        fontSize = 22.sp,
                        modifier = Modifier.padding(top = 10.dp, start = 20.dp),
                        color = Color.Black
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val nmlPercentage =
                        apneaCount?.let {
                            HourApneaCountChart(
                                apneaCount!!,
                                detailViewModel.apneaRecordSum
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 60.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            ChartLabelText(
                                labelText = "正常 ${(nmlPercentage?.times(100))}%",
                                color = Color(0xFF00FF0D)
                            )
//                            ChartLabelText(
//                                labelText = "S: ${1.toFloat() - (nmlPercentage?.times(100)!!)}次",
//                                color = Color(0xFFFF0000)
//                            )
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun HourArymaCountChart(arymaCount: DetailViewModel.StateData) {
    val xLabel = listOf("N", "S", "V", "F", "Q")
    val barData = listOf(
        getBarData(arymaCount.nState, 0, Color(0xFF002B5B)),
        getBarData(arymaCount.sState, 1, Color(0xFF2B4865)),
        getBarData(arymaCount.vState, 2, Color(0xFF256D85)),
        getBarData(arymaCount.fState, 3, Color(0xFF806D85)),
        getBarData(arymaCount.qState, 4, Color(0xFFB2B8FF))
    )
//    val barData = listOf(
//        getBarData(12, 0, Color(0xFF002B5B)),
//        getBarData(20, 1, Color(0xFF2B4865)),
//        getBarData(10, 2, Color(0xFF256D85)),
//        getBarData(5, 3, Color(0xFF806D85)),
//        getBarData(8, 4, Color(0xFFB2B8FF))
//    )
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
fun HourApneaCountChart(apneaCount: Int, sum: Int):Float {
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




