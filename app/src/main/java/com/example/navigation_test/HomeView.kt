package com.example.navigation_test

import android.content.ContentValues
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import java.lang.Integer.max


//首頁
var mblist = mutableListOf<String>()
var userId = mutableListOf<String>()
var ecgDataList = mutableListOf<List<Float>>()

@Composable
fun HomeView(name: String?, navController: NavController) {
    println("提早執行")
    val data = remember { mutableStateOf(mblist) }
    val ecgList = remember { mutableStateOf(ecgDataList) }
    val combinedData = remember {
        mutableStateOf(data.value.zip(ecgList.value).toMap())
    }
    val state = rememberReorderableLazyListState(onMove = { from, to ->
        data.value = data.value.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    })
    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(250.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(10.dp)
                    .width(200.dp)
                    .height(48.dp),
                elevation = 10.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            reFreshHomepage(navController)
                            navController.navigate("waiting")
                            println("按了重新整理")
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "同步人員名單", textAlign = TextAlign.Center, fontSize = 20.sp)
                }
            }
            Card(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .border(2.dp, Color.Blue)
                    .height(500.dp),
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
                            val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp)
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
                                        text = item,
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
            AlertDialogSample(navController)
        }

        LazyVerticalGrid(columns = GridCells.Adaptive(500.dp)) {
            items(data.value) { item ->
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
                            Text(text = item, fontSize = 40.sp)
                            Text(text = "State", fontSize = 25.sp)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            combinedData.value[item]?.let {
                                EcgPlot(
                                    ecgData = it,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(2f)
                                        .background(Color.Black)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}



