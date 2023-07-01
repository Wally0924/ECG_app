package com.example.navigation_test

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Patient(
    val name: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoticeView() {

    val patientList = listOf(
        Patient("hello"),
        Patient("world"),
        Patient("world"),
        Patient("world"),
        Patient("world"),
        Patient("world"),
        Patient("world"),
        Patient("world"),
        Patient("world"),
        Patient("world"),
        Patient("world"),
        Patient("world"),
        Patient("world"),
        Patient("world"),
        Patient("world"),
        Patient("world")
    )
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        PatientChatScreen(patients = patientList)
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
                    supportingText = { Text("XXX@gmail.com") },
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

