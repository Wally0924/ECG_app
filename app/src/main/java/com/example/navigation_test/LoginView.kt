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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignInView(
    navController: NavController,
    firebaseAuth: FirebaseAuth,
    signInClicked: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val emailState = remember { mutableStateOf(TextFieldValue()) }
    val passwordState = remember { mutableStateOf(TextFieldValue()) }
    val isLoading = remember { mutableStateOf(false) }
    var passwordVisibility by remember { mutableStateOf(false) }
    val icon = if (passwordVisibility)
        painterResource(id = R.drawable.baseline_visibility_24)
    else
        painterResource(id = R.drawable.baseline_visibility_off_24)

    fun loginUser(navController: NavController) {
        isLoading.value = true
        val email = emailState.value.text
        val password = passwordState.value.text
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                isLoading.value = false
                Toast.makeText(context, "登入成功", Toast.LENGTH_SHORT).show()
                navController.navigate("home")
            }
            .addOnFailureListener { exception ->
                isLoading.value = false
                Toast.makeText(context, "登入失敗 " + exception.message, Toast.LENGTH_SHORT).show()
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
        Text(text = "歡迎使用 心律檢測App", fontSize = 45.sp)
        Column(
            modifier = Modifier
                .padding(2.dp)
                .height(210.dp)
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
        Emptybar(25.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                modifier = Modifier
                    .padding(start = 30.dp, end = 30.dp)
                    .width(200.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(width = 1.5.dp, color = Color.Black),
                onClick = { loginUser(navController) },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White,
                    contentColor = Color.Black
                )
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
                        painter = painterResource(id = R.drawable.baseline_email_24),
                        contentDescription = "Login"
                    )
                    Text(
                        modifier = Modifier
                            .padding(start = 20.dp)
                            .align(Alignment.CenterVertically),
                        text = "登入",
                        fontSize = 20.sp
                    )
                }
            }
            Button(
                modifier = Modifier
                    .padding(start = 30.dp, end = 30.dp)
                    .width(200.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(width = 1.5.dp, color = Color.Black),
                onClick = { navController.navigate("register") },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White,
                    contentColor = Color.Black
                )
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
                        contentDescription = "register"
                    )
                    Text(
                        modifier = Modifier
                            .padding(start = 20.dp)
                            .align(Alignment.CenterVertically),
                        text = "註冊用戶",
                        fontSize = 20.sp
                    )
                }
            }
            Button(
                modifier = Modifier
                    .padding(start = 30.dp, end = 30.dp)
                    .width(200.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(width = 1.5.dp, color = Color.Black),
                onClick = {
                    signInClicked()
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        modifier = Modifier
                            .size(30.dp)
                            .padding(0.dp)
                            .align(Alignment.CenterVertically)
                            .border(2.dp, Color.Black),
                        painter = painterResource(id = R.drawable.baseline_g_mobiledata_24),
                        contentDescription = "google sign"
                    )
                    Text(
                        modifier = Modifier
                            .padding(start = 20.dp)
                            .align(Alignment.CenterVertically),
                        text = "google登入",
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}


@Composable
fun Emptybar(dpsize: Dp) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(dpsize)
    )
}

