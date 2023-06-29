package com.example.navigation_test

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Collections.list

@Composable
fun AlertDialogSample(navController: NavController) {
    val firebaseDb = FirebaseFirestore.getInstance()

    var member = MemberItem("", "", "", "")
    Column {
        val openDialog = remember { mutableStateOf(false) }
        val state by remember { mutableStateOf(FormState()) }
        Button(
            onClick = { openDialog.value = true },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .padding(10.dp)
                .width(200.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(Color.LightGray)
        ) {
            Image(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.fillMaxHeight()
            )
            Text(text = "新增人員", textAlign = TextAlign.Center, fontSize = 20.sp)
        }
        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    openDialog.value = false
                },
                text = {
                    //新增人員表單
                    Column(
                    ) {
                        Text(text = "新增人員", fontSize = 25.sp, modifier = Modifier.fillMaxWidth())
                        Form(
                            state = state,
                            fields = listOf(
                                Field(name = "姓名", KeyboardType.Text),
                                Field(name = "Email", KeyboardType.Email),
                                Field(name = "電話", KeyboardType.Phone),
                                Field(name = "地址", KeyboardType.Text)
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            openDialog.value = false
                            member = MemberItem(
                                state.mutableList[0],
                                state.mutableList[1],
                                state.mutableList[2],
                                state.mutableList[3]
                            )
                            val user = userInfo(state.mutableList[1],state.mutableList[3],state.mutableList[0],state.mutableList[2])
                            firebaseDb.collection("USER")
                                .add(user)
                                .addOnSuccessListener { documentReference ->
                                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error adding document", e)
                                }
                            navController.navigate("home/${member.name}")
                        }) {
                        Text(text = "加入")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            openDialog.value = false
                        }) {
                        Text(text = "取消")
                    }
                }
            )
        }
    }
}


class Field(val name: String, val kbType: KeyboardType) {
    var value: String by mutableStateOf("")

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun Content() {
        val keyboardController = LocalSoftwareKeyboardController.current
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            TextField(
                value = value,
                onValueChange = { newText ->
                    value = newText
                },
                label = { Text(text = name, fontSize = 18.sp, modifier = Modifier.fillMaxWidth()) },
                placeholder = { Text(text = "請輸入$name") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = kbType,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        // close the keyboard
                        keyboardController?.hide()
                    }
                )
            )
        }
    }

    fun getvalue(): String {
        return value
    }
}

class FormState {
    var mutableList: List<String> = listOf()
    var fields: List<Field> = listOf()
}

@Composable
fun Form(state: FormState, fields: List<Field>) {
    var memberdata: MutableList<String> = mutableListOf()
    state.fields = fields
    Column {
        fields.forEach {
            it.Content()
            memberdata.add(it.getvalue())
            println(memberdata)
        }
    }
    state.mutableList = memberdata
}


