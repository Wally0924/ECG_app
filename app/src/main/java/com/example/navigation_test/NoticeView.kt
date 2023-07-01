package com.example.navigation_test

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class Patient(
    val name: String,
    val email:String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoticeView() {
    val ptList = remember { mutableStateListOf<Patient>() } // 使用 mutableStateListOf 來創建可變的病患列表
    val firebaseDb = FirebaseFirestore.getInstance()
    val query = firebaseDb.collection("USER")

    // 使用 LaunchedEffect 在 Compose 中執行非同步查詢
    LaunchedEffect(Unit) {
        try {
            val querySnapshot = query.get().await() // 使用 await() 等待非同步操作完成

            for (document in querySnapshot) {
                val name = document.data["userName"] as? String
                val email = document.data["userEmail"] as? String
                if (name != null && email != null) {
                    ptList.add(Patient(name, email))
                }
            }
        } catch (exception: Exception) {
            Log.e("NoticeView", "Error getting documents: ", exception)
        }
    }

    Card(
        modifier = Modifier.fillMaxSize().padding(10.dp)
    ) {
        PatientChatScreen(patients = ptList)
    }
}


@Composable
fun PatientChatScreen(patients: List<Patient>) {
    val selectedPatient = remember { mutableStateOf<Patient?>(null) }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            PatientList(patients, selectedPatient.value) { patient ->
                selectedPatient.value = patient
            }
        }
        Column(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),modifier = Modifier.padding(start = 10.dp)) {
                selectedPatient.value?.let { patient ->
                    PatientChatRoom(patient)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientList(
    patients: List<Patient>,
    selectedPatient: Patient?,
    onPatientSelected: (Patient) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            onClick = { /* Do something! */ },
            modifier = Modifier.padding(5.dp),
            enabled = false,
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = MaterialTheme.colorScheme.primary,
                disabledContentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(text = "病患名單", fontSize = 18.sp)
        }
        LazyColumn {
            items(patients) { patient ->
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPatientSelected(patient) },
                    headlineText = { Text(patient.name) },
                    supportingText = { Text(patient.email) },
                    leadingContent = {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = "Localized description",
                        )
                    }
                )
                Divider()
            }
        }
    }
}

@Composable
fun PatientChatRoom(patient: Patient) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "聊天室",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = patient.name,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(16.dp)
        )
        // 在這裡添加聊天室的內容和互動元件，例如對話框、訊息列表等等
        // 可以使用Material Design 3的元件來設計聊天室介面
    }
}

