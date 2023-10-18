package com.example.navigation_test

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable


//首頁
var userId = mutableListOf<String>()
val chartViewModel = ChartViewModel()
val mbViewModel = MemberViewModel()

@Composable
fun HomeView(navController: NavController, email: String?) {
    val data = remember { mutableStateOf(userId) }
    val isLoading = remember { mutableStateOf(false) }
//    val ecgList = remember { mutableStateOf(ecgDataList) }
//    val combinedData = remember {
//        mutableStateOf(data.value.zip(ecgList.value).toMap())
//    }
    val state = rememberReorderableLazyListState(onMove = { from, to ->
        data.value = data.value.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    })
    if (isLoading.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Waiting()
        }
    } else {
        chartViewModel.initKey(userId)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "心律不整及睡眠呼吸中止辨識App",
                    fontSize = 25.sp,
                    modifier = Modifier.padding(start = 15.dp)
                )
                Text(
                    text = "歡迎使用",
                    fontSize = 22.sp,
                    modifier = Modifier
                        .width(400.dp)
                        .background(Color(0xFFE8DAFF), RoundedCornerShape(20.dp)),
                    textAlign = TextAlign.Center
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = "user",
                        modifier = Modifier
                            .size(30.dp)
                    )
                    Text(
                        text = email ?: "",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 10.dp, end = 15.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.padding(10.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(250.dp)
                        .padding(start = 15.dp, end = 5.dp, bottom = 5.dp)
                        .border(2.dp, Color.Blue, RoundedCornerShape(20.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .padding(10.dp)
                            .width(200.dp)
                            .weight(1f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    isLoading.value = true
                                    reFreshHomepage(mbViewModel) {
                                        isLoading.value = false
                                    }

                                }
                                .background(Color.LightGray),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "同步人員名單",
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp
                            )
                        }
                    }
                    Card(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
//                            .border(2.dp, Color.Blue)
                            .weight(8f),
//                shape = RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp),
//                backgroundColor = Color.Gray,
                        elevation = 10.dp,
                    ) {
                        LazyColumn(
                            state = state.listState,
                            modifier = Modifier
                                .reorderable(state)
                                .detectReorderAfterLongPress(state),
                        ) {
                            items(data.value, { it }) { item ->
                                ReorderableItem(state, key = item) { isDragging ->
                                    val elevation = animateDpAsState(
                                        if (isDragging) 16.dp else 0.dp,
                                        label = ""
                                    )
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .shadow(elevation.value)
                                            .background(MaterialTheme.colors.surface),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalArrangement = Arrangement.SpaceEvenly,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = mbViewModel.getListData(item),
                                                modifier = Modifier
                                                    .width(150.dp)
                                                    .padding(10.dp),
                                                fontSize = 32.sp
                                            )
                                            Icon(
                                                painter = painterResource(id = R.drawable.round_dehaze_24),
                                                contentDescription = null
                                            )
                                        }
                                        Divider(thickness = 2.dp)
                                    }
                                }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 5.dp)
                    ) {
                        AlertDialogSample(navController)
                    }
                }
                ChartList(
                    data = data.value,
                    chartViewModel = chartViewModel,
                    navController = navController
                )
            }
        }
    }
}



