package com.example.timur3

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.timur3.ui.theme.Timur3Theme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        setContent {
            Timur3Theme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "splash") {
                    composable("splash") {
                        SplashScreen {
                            navController.navigate("login")
                        }
                    }
                    composable("login") {
                        LoginScreen(auth, navController)
                    }
                    composable("registration") {
                        RegistrationScreen(auth) {
                            navController.navigate("login") // Возврат на экран входа после регистрации
                        }
                    }
                    composable("onboard") {
                        OnboardScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onNavigateToOnboard: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 3000
        )
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(3000)
        onNavigateToOnboard()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "MATULE me",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(16.dp)
                .alpha(alpha = alphaAnim)
        )
    }
}

@Composable
fun OnboardScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Onboard Screen")
    }
}

@Composable
fun LoginScreen(auth: FirebaseAuth, navController: NavController) {
    val emailState = remember { mutableStateOf(TextFieldValue()) }
    val passwordState = remember { mutableStateOf(TextFieldValue()) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Привет!", style = MaterialTheme.typography.titleLarge)
        Text(
            text = "Заполните свои данные или продолжите через социальные медиа",
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        TextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            TextField(
                value = passwordState.value,
                onValueChange = { passwordState.value = it },
                label = { Text("Пароль") },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = if (isPasswordVisible) "Скрыть пароль" else "Показать пароль",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { isPasswordVisible = !isPasswordVisible }
                    .padding(end = 8.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (validateInput(emailState.value.text, passwordState.value.text)) {
                    loginUser(auth, emailState.value.text, passwordState.value.text) { success, message ->
                        loginError = message
                        if (success) {
                            navController.navigate("onboard") // Переход на экран Onboard
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Пожалуйста, проверьте введенные данные.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Войти")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (loginError.isNotEmpty()) {
            Text(text = loginError, color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(
            text = "Восстановить аккаунт",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable { /* TODO: Handle account recovery logic */ }
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Нет аккаунта? Зарегистрироваться",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable { navController.navigate("registration") } // Переход на экран регистрации
                .padding(8.dp)
        )
    }
}

@Composable
fun RegistrationScreen(auth: FirebaseAuth, onRegistrationSuccess: () -> Unit) {
    val emailState = remember { mutableStateOf(TextFieldValue()) }
    val passwordState = remember { mutableStateOf(TextFieldValue()) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Регистрация", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(32.dp))

        TextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            label = { Text("Пароль") },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = if (isPasswordVisible) "Скрыть пароль" else "Показать пароль",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable { isPasswordVisible = !isPasswordVisible }
                .padding(end = 8.dp),
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (validateInput(emailState.value.text, passwordState.value.text)) {
                    registerUser(auth, emailState.value.text, passwordState.value.text) { success, message ->
                        if (success) {
                            onRegistrationSuccess() // Переход на экран входа
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Пожалуйста, проверьте введенные данные.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Зарегистрироваться")
        }
    }
}

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Добро пожаловать в Home!", style = MaterialTheme.typography.titleLarge)
    }
}

private fun validateInput(email: String, password: String): Boolean {
    val emailPattern = "[a-z0-9]+@[a-z0-9]+\\.[a-z]{2,}"
    return email.matches(Regex(emailPattern)) && email.isNotEmpty() && password.isNotEmpty()
}

private fun loginUser(auth: FirebaseAuth, email: String, password: String, onResult: (Boolean, String) -> Unit) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(true, "Успешный вход")
            } else {
                onResult(false, task.exception?.message ?: "Ошибка входа")
            }
        }
}

private fun registerUser(auth: FirebaseAuth, email: String, password: String, onResult: (Boolean, String) -> Unit) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(true, "Успешная регистрация")
            } else {
                onResult(false, task.exception?.message ?: "Ошибка регистрации")
            }
        }
}
