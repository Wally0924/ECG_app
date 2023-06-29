package com.example.navigation_test


import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignUpView(
    navController: NavController,
    firebaseAuth: FirebaseAuth
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val emailState = remember { mutableStateOf(TextFieldValue()) }
    val passwordState = remember { mutableStateOf(TextFieldValue()) }
    val confirmpasswordState = remember { mutableStateOf(TextFieldValue()) }
    val isLoading = remember { mutableStateOf(false) }
    var passwordVisibility by remember { mutableStateOf(false) }
    val icon = if (passwordVisibility)
        painterResource(id = R.drawable.baseline_visibility_24)
    else
        painterResource(id = R.drawable.baseline_visibility_off_24)

    fun createUser(navController: NavController) {
        isLoading.value = true
        val email = emailState.value.text
        val password = passwordState.value.text
        val confirmpassword = confirmpasswordState.value.text
        if (password == confirmpassword) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    isLoading.value = false
                    Toast.makeText(context, "註冊成功", Toast.LENGTH_SHORT).show()
                    navController.navigate("signIn")
                }
                .addOnFailureListener { exception ->
                    isLoading.value = false
                    Toast.makeText(context, "註冊失敗 " + exception.message, Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "確認密碼有誤，請重新檢查", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .size(200.dp)
                .padding(0.dp),
            painter = painterResource(id = R.mipmap.app_image_foreground),
            contentDescription = "app icon"
        )
        Text(text = "註冊帳戶", fontSize = 45.sp)
        Column(
            modifier = Modifier
                .padding(2.dp)
                .height(250.dp)
                .fillMaxWidth(),
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedTextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                label = { Text(text = "Email") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email
                ),
                singleLine = true,
                modifier = Modifier.height(60.dp)
            )
            OutlinedTextField(
                value = passwordState.value,
                onValueChange = { passwordState.value = it },
                label = { Text(text = "Password") },
                visualTransformation = if (passwordVisibility) VisualTransformation.None
                else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = {
                        passwordVisibility = !passwordVisibility
                    }) {
                        Icon(
                            painter = icon,
                            contentDescription = "Visibility Icon"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Password
                ),
                singleLine = true,
                modifier = Modifier.height(60.dp)
            )
            OutlinedTextField(
                value = confirmpasswordState.value,
                onValueChange = { confirmpasswordState.value = it },
                label = { Text(text = "Confirm password") },
                visualTransformation = if (passwordVisibility) VisualTransformation.None
                else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = {
                        passwordVisibility = !passwordVisibility
                    }) {
                        Icon(
                            painter = icon,
                            contentDescription = "Visibility Icon"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        // close the keyboard
                        keyboardController?.hide()
                    }
                ),
                singleLine = true,
                modifier = Modifier.height(60.dp)
            )
        }
        Emptybar(30.dp)
        Card(
            modifier = Modifier
                .padding(start = 30.dp, end = 30.dp)
                .width(140.dp)
                .height(50.dp)
                .clickable {
                    createUser(navController)
                },
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(width = 1.5.dp, color = Color.Black),
            elevation = 5.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    modifier = Modifier
                        .size(34.dp)
                        .padding(0.dp)
                        .align(Alignment.CenterVertically),
                    painter = painterResource(id = R.drawable.baseline_account_box_24),
                    contentDescription = "Login"
                )
                Text(
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .align(Alignment.CenterVertically),
                    text = "註冊",
                    fontSize = 20.sp
                )
            }
        }

    }
}


