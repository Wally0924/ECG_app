package com.example.navigation_test

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.navigation_test.ui.theme.Navigation_testTheme
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MainActivity : ComponentActivity() {
    private lateinit var mAuth: FirebaseAuth
    private val Lgwith = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
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
    Row {
        NavRail(navController = navController) { signOutClicked() }
        Scaffold(
            scaffoldState = scaffoldState,
//            topBar = {
//                TopAppBar(
//                    title = { Text(text = "") },
//                    backgroundColor = Color(0xFF8E58E9),
//                    actions = {
//                        Row(
//                            modifier = Modifier.padding(start = 20.dp),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Icon(
//                                Icons.Filled.Person,
//                                contentDescription = "user",
//                                modifier = Modifier
//                                    .size(35.dp)
//                                    .padding(end = 6.dp)
//                            )
//                            if (email != null) {
//                                Text(
//                                    text = email,
//                                    fontSize = 20.sp,
//                                    modifier = Modifier.padding(end = 20.dp)
//                                )
//                            }
//                        }
//                    }
//                )
//            }
        ) {
            Navigation(navController = navController, email = email)
        }
    }
}

@Composable
fun NavRail(navController: NavHostController, signOutClicked: () -> Unit) {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("首頁", "通知", "設定", "幫助", "登出")
    val icons = listOf(
        Icons.Filled.Home,
        Icons.Filled.Notifications,
        Icons.Filled.Settings,
        Icons.Filled.Info,
        Icons.Filled.ExitToApp
    )
    NavigationRail(
        modifier = Modifier.shadow(
            elevation = 15.dp,
            clip = true
        ),
        header = {
            Text(
                text = "功能",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 45.dp)
            )
        },
        containerColor = Color(0xFFE8DAFF)
    ) {
        items.forEachIndexed { index, item ->
            NavigationRailItem(
                modifier = Modifier.padding(top = 5.dp, end = 5.dp),
                icon = {
                    Icon(
                        icons[index],
                        contentDescription = item,
                        modifier = Modifier.size(25.dp)
                    )
                },
                label = { Text(item, fontWeight = FontWeight.Bold, fontSize = 15.sp) },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    // 根據需要進行導航
                    when (index) {
                        0 -> navController.navigate("home")
                        1 -> navController.navigate("notice")
                        2 -> navController.navigate("settings")
                        3 -> navController.navigate("help")
                        4 -> {
                            signOutClicked()
                        }
                    }
                }
            )
            if (index == items.size - 2) { // 倒數第二個項目
                Spacer(Modifier.height(300.dp)) // 使用 Spacer 產生間隔
            }
        }
    }
}


@Composable
fun Navigation(navController: NavHostController, email: String?) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            Log.d("ChartView", "Home這裡檢查有沒有被重繪")
            HomeView(navController, email)
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
        composable("waiting") {
            Log.d("ChartView", "Waiting這裡檢查有沒有被重繪")
            Waiting()
        }
        composable("notice") {
            NoticeView()
        }
    }
}


@Composable
fun HelpScreen() {
    Button(
        onClick = {
            val currentTime = Timestamp.now()
            val sevenDayAgoTime = sevenDayAgo().let { timestamp ->
                Timestamp(timestamp.seconds, timestamp.nanoseconds)
            }
            Log.d("DetailViewModel", "sevenDayAgoTime: $sevenDayAgoTime")
            Log.d("DetailViewModel", "currentTime: $currentTime")
        },
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
    ) {
        Text(text = "Help")
    }
}
fun sevenDayAgo(): Timestamp {
    // 創建一個 Calendar 實例
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))

    // 計算並設定 7 天前的日期
    calendar.add(Calendar.DAY_OF_MONTH, -6)

    // 將日期設定為當天的0時0分0秒
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    // 創建一個 Timestamp 物件，表示 7 天前的日期的0時
    return Timestamp((calendar.time))
}


private fun requestSmsPermission(context: Context, onPermissionResult: (Boolean) -> Unit) {
    if (ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        onPermissionResult(true)
    } else {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(android.Manifest.permission.SEND_SMS),
            100
        )
    }
}


@Composable
fun SettingsScreen() {
    Log.d("SettingScreen", "SettingScreen重新繪製")
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



