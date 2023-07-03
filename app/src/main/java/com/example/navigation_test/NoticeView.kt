package com.example.navigation_test

import android.content.ContentValues
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class Patient(
    val id : String,
    val name: String,
    val email: String,
    val address: String,
    val phone: String
)
data class FormData(
    val name: String,
    val email: String,
    val address: String,
    val phone: String,
    val rhythmType: String,
    val note: String
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
                val id = document.id
                val name = document.data["userName"] as? String
                val email = document.data["userEmail"] as? String
                val address = document.data["userAddress"] as? String
                val phone = document.data["userPhone"] as? String
                if (name != null && email != null && address != null && phone != null) {
                    ptList.add(Patient(id,name, email, address, phone))
                }
            }
        } catch (exception: Exception) {
            Log.e("NoticeView", "Error getting documents: ", exception)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
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
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
                modifier = Modifier.padding(start = 40.dp, top = 20.dp, end = 20.dp)
            ) {
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun PatientChatRoom(patient: Patient) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val radioOptions = listOf("LBBB", "RBBB", "VPC", "APC")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    val context = LocalContext.current

    fun onSubmitClicked(patient:Patient) {
        val db = FirebaseFirestore.getInstance()
        val formData = FormData(
            name = name,
            email = email,
            address = address,
            phone = phone,
            rhythmType = selectedOption,
            note = note
        )
        // 將 formData 物件存入 Firestore
        db.collection("USER").document(patient.id).collection("Notice")
            .add(formData)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "送出通知成功")
                Toast.makeText(context, "送出通知成功", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.w(ContentValues.TAG, "通知失敗")
                Toast.makeText(context, "通知失敗", Toast.LENGTH_SHORT).show()
            }
    }
    LaunchedEffect(patient) {
        name = patient.name
        email = patient.email
        address = patient.address
        phone = patient.phone
    }
    Text(
        text = "發送通知",
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier.padding(16.dp)
    )
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, bottom = 20.dp),
        thickness = 2.dp
    )
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(
                value = name,
                onValueChange = { /* 不需要執行任何操作 */ },
                label = { Text("姓名") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                readOnly = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "姓名圖示"
                    )
                }
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            )
            OutlinedTextField(
                value = email,
                onValueChange = { /* 不需要執行任何操作 */ },
                label = { Text("電子郵件") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                readOnly = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "電子郵件圖示"
                    )
                }
            )

        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = address,
                onValueChange = { /* 不需要執行任何操作 */ },
                label = { Text("地址") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                readOnly = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "地址圖示"
                    )
                }
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { /* 不需要執行任何操作 */ },
                label = { Text("電話") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                readOnly = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "電話圖示"
                    )
                }
            )
        }
    }
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
    )
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "心律不整類型",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(start = 20.dp)
            )
            Row(
                Modifier
                    .selectableGroup()
                    .padding(start = 30.dp, end = 30.dp)
            ) {
                radioOptions.forEach { text ->
                    Row(
                        Modifier
                            .weight(1f)
                            .height(40.dp)
                            .selectable(
                                selected = (text == selectedOption),
                                onClick = { onOptionSelected(text) },
                                role = Role.RadioButton
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (text == selectedOption),
                            onClick = null
                        )
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }
    }
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
    )
    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = note,
                onValueChange = {
                    note = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(start = 35.dp, end = 35.dp),
                label = { Text("備註") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        // close the keyboard
                        keyboardController?.hide()
                    }
                )
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            )
            Button(
                onClick = { onSubmitClicked(patient)},
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding
            ) {
                Text("送出" , fontSize = 16.sp)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Icon(
                    Icons.Filled.Send,
                    contentDescription = "Localized description",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
            }
        }
    }
}



