package com.example.navigation_test

import android.content.ContentValues
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun DetailPage(
    title: String? = "",
    content: String? = "",
    usrId: String,
    dbViewModel: DataBaseViewModel,
    chartViewModel: ChartViewModel,
    state: String,
    onDismissRequest: () -> Unit
) {
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
                    .padding(top = 30.dp, start = 30.dp, bottom = 10.dp,end = 30.dp),
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
                    modifier = Modifier.weight(2f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    ChartView(chartViewModel, dbViewModel, usrId)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 20.dp)
                ) {
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
                            text = "心律辨識狀態: $state ",
                            fontSize = 22.sp,
                            modifier = Modifier.padding(10.dp), color = Color.Black
                        )
                    }
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
                            text = "睡眠呼吸中止辨識狀態: 良好",
                            fontSize = 22.sp,
                            modifier = Modifier.padding(10.dp), color = Color.Black
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                        text = "睡眠呼吸中止辨識狀態: 良好",
                        fontSize = 22.sp,
                        modifier = Modifier.padding(10.dp), color = Color.Black
                    )
                }
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
                        text = "睡眠呼吸中止辨識狀態: 良好",
                        fontSize = 22.sp,
                        modifier = Modifier.padding(10.dp), color = Color.Black
                    )
                }
            }

        }
    }
}



