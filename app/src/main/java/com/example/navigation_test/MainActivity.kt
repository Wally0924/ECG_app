package com.example.navigation_test

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.navigation_test.ui.theme.Navigation_testTheme
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition


class MainActivity : ComponentActivity() {
    private val Lgwith = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            Toast.makeText(this, "登入成功", Toast.LENGTH_SHORT).show()
            setContent {
                Navigation_testTheme {
                    MainView(user?.email!!) {
                        signOut()
                    }
                }
            }
        } else {
            Toast.makeText(this, "登入失敗", Toast.LENGTH_SHORT).show()
        }
    }

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // firebase auth instance
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        setContent {
            Navigation_testTheme {
                if (currentUser == null) {
                    val navController = rememberNavController()
                    LoginNav(navController = navController)
                } else {
                    MainView(currentUser.email!!) {
                        signOut()
                    }
                }
            }
        }
    }

    private fun signIn() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTheme(R.style.LoginTheme)
            .setIsSmartLockEnabled(false)
            .build()

        Lgwith.launch(signInIntent)
    }

    private fun signOut() {
        AuthUI.getInstance().signOut(this)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "登出成功", Toast.LENGTH_SHORT).show()
                    setContent {
                        val navController = rememberNavController()
                        Navigation_testTheme {
                            LoginNav(navController = navController)
                        }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "登出失敗", Toast.LENGTH_SHORT).show()
            }
        this@MainActivity.setContentView(R.layout.activity_main)
    }

    @Composable
    private fun LoginNav(navController: NavHostController) {
        NavHost(
            navController = navController,
            startDestination = "signIn"
        ) {
            composable("signIn") {
                SignInView(navController, mAuth) {
                    signIn()
                }
            }
            composable("home") {
                MainView(mAuth.currentUser?.email!!) {
                    signOut()
                }
            }
            composable("register") {
                SignUpView(navController, mAuth)
            }
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainView(email: String?, signOutClicked: () -> Unit) {
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = "心律檢視") },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary,
                navigationIcon = {
                    IconButton(onClick = { scope.launch { scaffoldState.drawerState.open() } }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Toggle drawer"
                        )
                    }
                },
                actions = {
                    Row(
                        modifier = Modifier.padding(start = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (email != null) {
                            Text(
                                text = email,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(end = 20.dp)
                            )
                        }
                        Button(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(end = 50.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.White,
                                contentColor = Color.Black
                            ),
                            onClick = { signOutClicked() }
                        ) {
                            Text(text = "登出", fontSize = 15.sp)
                        }
                    }
                }
            )
        },
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        drawerContent = {
            DrawerHeader()
            DrawerBody(items = listOf(
                MenuItem(
                    id = "home/ ",
                    title = "首頁",
                    contentDescription = "Go to home screen",
                    icon = Icons.Default.Home
                ),
                MenuItem(
                    id = "settings",
                    title = "設定",
                    contentDescription = "Go to settings screen",
                    icon = Icons.Default.Settings
                ),
                MenuItem(
                    id = "help",
                    title = "幫助",
                    contentDescription = "Go to help screen",
                    icon = Icons.Default.Notifications
                )
            ), onItemClick = {
                navController.navigate(it.id)
                scope.launch {
                    scaffoldState.drawerState.close()
                }
            })
        }) {
        Navigation(navController = navController)
    }
}


@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home" + "/{name}") {
        composable("home" + "/{name}", arguments = listOf(navArgument("name") {
            type = NavType.StringType
            nullable = true
        })) { entry ->
            HomeView(name = entry.arguments?.getString("name"), navController)
        }
        composable("addMember") {
            AlertDialogSample(navController)
        }
        composable("help") {
            HelpScreen()
        }
        composable("settings") {
            SettingsScreen()
        }
        composable( "waiting"){
            Waiting()
        }
    }
}



@Composable
fun HelpScreen() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lotie))
    var isPlaying by remember { mutableStateOf(true) }

    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        iterations = LottieConstants.IterateForever
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            modifier = Modifier.size(400.dp),
            composition = composition,
            progress = progress
        )
        Button(onClick = { isPlaying = !isPlaying }) {
            Text(text = if (isPlaying) "停止" else "播放")
        }
    }
}


@Composable
fun SettingsScreen() {
    val notification_set = remember { mutableStateOf("On") }
    val darkmode_set = remember { mutableStateOf("Off") }
    val language_set = remember { mutableStateOf("English") }
    Card(
        modifier = Modifier
            .padding(20.dp)
            .aspectRatio(2f)
            .fillMaxSize(),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(2.dp, Color.Blue)
    ) {
        Column {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Settings",
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    fontSize = 50.sp,
                    textAlign = TextAlign.Center
                )
            }
            Divider(color = Color.Gray, modifier = Modifier.height(2.dp))
            SettingRow("Notifications", notification_set.value) {
                notification_set.value = if (notification_set.value == "On") "Off" else "On"
            }
            Divider()
            SettingRow("Dark mode", darkmode_set.value) {
                darkmode_set.value = if (darkmode_set.value == "On") "Off" else "On"

            }
            Divider()
            SettingRow("Language", language_set.value) {
                language_set.value =
                    if (language_set.value == "English") "Chinese" else "English"
            }
            Divider()
        }
    }
}

@Composable
fun SettingRow(title: String, value: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                title,
                style = TextStyle(fontWeight = FontWeight.Bold),
                fontSize = 30.sp,
                textAlign = TextAlign.Center
            )
            Text(value, fontSize = 20.sp, textAlign = TextAlign.Center)
        }
    }
}



