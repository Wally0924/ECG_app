package com.example.navigation_test

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun DetailPage(
    usrId: String,
    dbViewModel: DataBaseViewModel,
    chartViewModel: ChartViewModel,
    state: String,
    apneaState: String,
    onDismissRequest: () -> Unit
) {
    val detailViewModel = remember(usrId) { DetailViewModel(usrId) }

    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .height(700.dp)
                .width(1000.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, start = 30.dp, bottom = 10.dp, end = 30.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = mbViewModel.getListData(usrId),
                    fontSize = 45.sp,
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
            TabField(
                detailViewModel,
                dbViewModel,
                state,
                apneaState, usrId
            )
        }
    }
}

//滑動頁面
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabField(
    detailViewModel: DetailViewModel,
    dbViewModel: DataBaseViewModel,
    state: String,
    apneaState: String,
    usrId: String
) {
    val tabItems = listOf(
        TabItem(
            title = "即時狀態",
            unselectIcon = ImageVector.vectorResource(id = R.drawable.baseline_access_time_24),
            selectedIcon = ImageVector.vectorResource(id = R.drawable.baseline_access_time_filled_24)
        ),
        TabItem(
            title = "病患歷史狀態分佈",
            unselectIcon = Icons.Outlined.Info,
            selectedIcon = Icons.Filled.Info
        )
    )
    var demoCheck by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState { tabItems.size }
    val selectDate = remember { mutableStateOf(getCurrentDate()) }
    val apneaCount by detailViewModel.apneaCount.observeAsState()
    val arymaCount by detailViewModel.arymaCount.observeAsState()
    val dataComplete by detailViewModel.dataComplete.observeAsState()
    val sevenDaysData by detailViewModel.sevenDaysData.observeAsState()
    val sevenDaysApnea by detailViewModel.sevenDaysApnea.observeAsState()
    val dateRange = (-6..0).map {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, it)
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("GMT+8")
        }
        format.format(calendar.time)
    }
    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }
    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 10.dp, end = 10.dp, bottom = 15.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFE6E6F2))
    ) {
        TabRow(selectedTabIndex = selectedTabIndex, containerColor = Color(0xFFE6E6F2)) {
            tabItems.forEachIndexed { index, tabItem ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (selectedTabIndex == index) tabItem.selectedIcon else tabItem.unselectIcon,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.padding(start = 5.dp))
                            Text(text = tabItem.title, fontSize = 18.sp)
                        }
                    }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
        ) { index ->
            when (index) {
                0 ->
                    //即時狀態
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .padding(15.dp),
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
                                            color = if (state == "Normal") Color.Green else if (state == "尚未連線") Color.Black else Color.Red
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
                                            text = apneaState,
                                            fontSize = 30.sp,
                                            modifier = Modifier.padding(
                                                top = 10.dp,
                                                start = 20.dp,
                                                bottom = 10.dp,
                                                end = 20.dp,
                                            ),
                                            color = if (apneaState == "正常") Color.Green else if (apneaState == "尚未連線" || apneaState == "測量中") Color.Black else Color.Red
                                        )
                                    }

                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
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
                                if (dataComplete == false) {
                                    Loading()
                                } else {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(start = 20.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        arymaCount?.let { HourArymaCountChart(it) }
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(start = 10.dp),
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            ChartLabelText(
                                                labelText = "N: ${arymaCount?.nState}次",
                                                color = Color(0xFF28FF28)
                                            )
                                            ChartLabelText(
                                                labelText = "S: ${arymaCount?.sState}次",
                                                color = Color(0xFFF9F900)
                                            )
                                            ChartLabelText(
                                                labelText = "V: ${arymaCount?.vState}次",
                                                color = Color(0xFFFFAA00)
                                            )
                                            ChartLabelText(
                                                labelText = "F: ${arymaCount?.fState}次",
                                                color = Color(0xFFFF6600)
                                            )
                                            ChartLabelText(
                                                labelText = "Q: ${arymaCount?.qState}次",
                                                color = Color(0xFFFF0000)
                                            )
                                        }
                                    }
                                }
                            }
                            //圖下右Card
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
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
                                if (dataComplete == false) {
                                    Loading()
                                } else {
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
                                            if (nmlPercentage != null) {
                                                ChartLabelText(
                                                    labelText = "正常 ${
                                                        (nmlPercentage * 100).toBigDecimal()
                                                            .setScale(2, RoundingMode.HALF_UP)
                                                            .toDouble()
                                                    }%",
                                                    color = Color(0xFF00FF0D)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                1 ->
                    //病患歷史狀態分佈
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, start = 10.dp, end = 10.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(onClick = { demoCheck = !demoCheck }) {
                                Text(text = if(demoCheck) "DEMO ON" else "DEMO OFF")
                            }
                        }
                        Text(
                            text = "心律狀況",
                            fontSize = 22.sp,
                            modifier = Modifier
                                .width(200.dp)
                                .padding(start = 40.dp, top = 30.dp)
                                .background(Color.White, shape = RoundedCornerShape(20.dp))
                                .border(1.dp, Color(0XFF8600FF), RoundedCornerShape(20.dp)),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .padding(15.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                IconButton(
                                    onClick = {
                                        if (dateRange.indexOf(selectDate.value) != 0)
                                            selectDate.value =
                                                dateRange[dateRange.indexOf(selectDate.value) - 1]
                                    },
                                    modifier = Modifier
                                        .size(60.dp)
                                        .padding(start = 10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.KeyboardArrowLeft,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                                Text(
                                    text =
                                    if (selectDate.value == getCurrentDate())
                                        "今日" else selectDate.value,
                                    fontSize = 23.sp,
                                    modifier = Modifier
                                        .width(200.dp)
                                        .padding(
                                            top = 10.dp
                                        ),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                )
                                IconButton(
                                    onClick = {
                                        if (dateRange.indexOf(selectDate.value) != dateRange.size - 1)
                                            selectDate.value =
                                                dateRange[dateRange.indexOf(selectDate.value) + 1]
                                    },
                                    modifier = Modifier.size(60.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                sevenDaysData?.let {
                                    VerticalStackedBarChart(
                                        demoCheck,
                                        selectDate.value,
                                        it
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                HistoryChartLabelText(
                                    labelText = "N",
                                    color = Color(0xFF28FF28)
                                )
                                HistoryChartLabelText(
                                    labelText = "S",
                                    color = Color(0xFFF9F900)
                                )
                                HistoryChartLabelText(
                                    labelText = "V",
                                    color = Color(0xFFFFAA00)
                                )
                                HistoryChartLabelText(
                                    labelText = "F",
                                    color = Color(0xFFFF6600)
                                )
                                HistoryChartLabelText(
                                    labelText = "Q",
                                    color = Color(0xFFFF0000)
                                )
                            }
                        }
                        Text(
                            text = "睡眠呼吸中止狀況",
                            fontSize = 22.sp,
                            modifier = Modifier
                                .width(300.dp)
                                .padding(start = 40.dp, top = 10.dp)
                                .background(Color.White, shape = RoundedCornerShape(20.dp))
                                .border(1.dp, Color(0XFF8600FF), RoundedCornerShape(20.dp)),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1550.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .padding(15.dp)
                        ) {
                            Text(
                                text = "病患歷史狀態分佈",
                                fontSize = 22.sp,
                                modifier = Modifier.padding(
                                    top = 10.dp,
                                    start = 10.dp,
                                    bottom = 30.dp,
                                    end = 10.dp
                                ),
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                ApneaStateLabel("正常 AHI<5 次/小時", Color.Green)
                                ApneaStateLabel("輕度 5≤AHI<15 次/小時", Color.Yellow)
                                ApneaStateLabel("重度 AHI≥15次/小時", Color.Red)
                            }
                            sevenDaysApnea?.let { SleepMeasurementScreen(dateRange,demoCheck, it) }
                        }
                    }
            }
        }
    }

}

fun dateFormat(date: Date): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
}

fun getCurrentDate(): String {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("GMT+8")
    }
    return format.format(System.currentTimeMillis())
}







