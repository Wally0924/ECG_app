package com.example.navigation_test

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

data class SleepMeasurement(
    val date: String
)

@Composable
fun SleepMeasurementList(
    sleepMeasurements: List<SleepMeasurement>,
    demoCheck: Boolean,
    sevenDaysApnea: Map<String, List<Int>>
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(sleepMeasurements) { item ->
            if (demoCheck)
                SleepMeasurementItem(item, null)
            else {
                if (!sevenDaysApnea.containsKey(item.date))
                    SleepMeasurementItem(item, listOf(1))
                else {
                    SleepMeasurementItem(item, sevenDaysApnea[item.date])
                }
            }
        }
    }
}

@Composable
fun SleepMeasurementItem(item: SleepMeasurement, ints: List<Int>?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon for date
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = null,
            tint = Color(0XFF8600FF),
            modifier = Modifier.size(30.dp)
        )

        // Spacer for separation
        Spacer(modifier = Modifier.width(16.dp))

        // Date
        Text(text = item.date, fontWeight = FontWeight.Bold, fontSize = 25.sp)
    }
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .width(800.dp)
                .padding(top = 50.dp, bottom = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val colors = listOf(Color.Red, Color.Yellow, Color.Green, Color.Transparent)
            if (ints == null) {
                repeat(24) {
                    val count = Random.nextInt(1, 1000)
                    val color = colors[count % 4]

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(color)
                    ) {
                        Text(text = "")
                    }
                }
            }
            else if(ints.size == 1){
                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = Color(0XFF8600FF), modifier = Modifier.size(30.dp))
                Text(text = "無資料", fontSize = 23.sp)
            }
            else {
                for ( item in ints) {
                    val color =
                        if (item == 0) colors[3] else if (item < 5) colors[2] else if (item < 15) colors[1] else colors[0]
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(color)
                    ) {
                        Text(text = "")
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .width(800.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "0時", fontSize = 15.sp, textAlign = TextAlign.Center)
//            Text(text = "3時", fontSize = 15.sp)
            Text(text = "6時", fontSize = 15.sp, textAlign = TextAlign.Center)
//            Text(text = "9時", fontSize = 15.sp)
            Text(text = "12時", fontSize = 15.sp, textAlign = TextAlign.Center)
//            Text(text = "15時", fontSize = 15.sp)
            Text(text = "18時", fontSize = 15.sp, textAlign = TextAlign.Center)
//            Text(text = "21時", fontSize = 15.sp)
            Text(text = "24時", fontSize = 15.sp, textAlign = TextAlign.Center)
        }
        Divider(
            modifier = Modifier
                .width(850.dp)
                .padding(top = 25.dp, bottom = 25.dp),
            color = Color(0XFF8600FF)
        )
    }

}

@Composable
fun SleepMeasurementScreen(
    dateRange: List<String>,
    demoCheck: Boolean,
    sevenDaysApnea: Map<String, List<Int>>
) {
    val sleepMeasurements = remember {
        val modifiedList = mutableListOf<SleepMeasurement>()
        // 依照 dateRange 修改日期
        for (date in dateRange) {
            modifiedList.add(SleepMeasurement(date))
        }
        modifiedList
    }
    SleepMeasurementList(sleepMeasurements, demoCheck, sevenDaysApnea)
}

