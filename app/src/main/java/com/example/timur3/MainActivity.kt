package com.example.timur3

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                NavHost(navController, startDestination = "login") {
                    composable("login") { LoginScreen(auth, navController) }
                    composable("splash") { SplashScreen { navController.navigate("onboard") } }
                    composable("onboard") { OnboardingScreen(onNavigateToHome = { navController.navigate("home") }) }
                    composable("home") { HomeScreen(navController) }

                    composable("registration") { RegistrationScreen(auth) { navController.navigate("login") } }
                    composable("catalog") { CatalogScreen() }
                    composable("popular") { PopularScreen(navController) }
                    composable("favorites") { FavoritesScreen() }
                }
            }
        }
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
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Привет!", style = MaterialTheme.typography.titleLarge)
        Text(text = "Заполните свои данные или продолжите через социальные медиа", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(32.dp))

        TextField(value = emailState.value, onValueChange = { emailState.value = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())

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
                modifier = Modifier.clickable { isPasswordVisible = !isPasswordVisible }.padding(end = 8.dp),
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
                            navController.navigate("splash") // Переход на экран Splash
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
            modifier = Modifier.clickable { /* TODO: Handle account recovery logic */ }.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Нет аккаунта? Зарегистрироваться",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { navController.navigate("registration") }.padding(8.dp)
        )
    }
}

@Composable
fun SplashScreen(onNavigateToOnboard: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 3000)
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
fun OnboardingScreen(onNavigateToHome: () -> Unit) {
    var currentScreen by remember { mutableStateOf(0) }
    val progressAlpha1 by animateFloatAsState(targetValue = if (currentScreen >= 0) 1f else 0f, animationSpec = tween(durationMillis = 300))
    val progressAlpha2 by animateFloatAsState(targetValue = if (currentScreen >= 1) 1f else 0f, animationSpec = tween(durationMillis = 300))
    val progressAlpha3 by animateFloatAsState(targetValue = if (currentScreen >= 2) 1f else 0f, animationSpec = tween(durationMillis = 300))

    Box(modifier = Modifier.fillMaxSize().background(color = Color(0xFF0077B6))) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            when (currentScreen) {
                0 -> {
                    Text(text = "Добро пожаловать", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(painter = painterResource(id = R.drawable.shoe1), contentDescription = "Shoe 1", modifier = Modifier.size(200.dp))
                }
                1 -> {
                    Text(text = "Начнем путешествие", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(painter = painterResource(id = R.drawable.image_2), contentDescription = "Shoe 2", modifier = Modifier.size(200.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Умная, великолепная и модная коллекция\nИзучайте сейчас", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Normal, textAlign = TextAlign.Center)
                }
                2 -> {
                    Text(text = "У Вас Есть Сила, Чтобы", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(painter = painterResource(id = R.drawable.image_3), contentDescription = "Shoe 3", modifier = Modifier.size(200.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "В вашей комнате много красивых и привлекательных растений", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Normal, textAlign = TextAlign.Center)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(12.dp).background(color = Color.Gray.copy(alpha = progressAlpha1), shape = CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.size(12.dp).background(color = Color.Gray.copy(alpha = progressAlpha2), shape = CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.size(12.dp).background(color = Color.Gray.copy(alpha = progressAlpha3), shape = CircleShape))
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    if (currentScreen == 2) {
                        onNavigateToHome() // Переход на экран Home
                    } else {
                        currentScreen = (currentScreen + 1) % 3
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF0077B6))
            ) {
                Text(text = if (currentScreen == 2) "Начать" else "Далее", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun RegistrationScreen(auth: FirebaseAuth, onRegistrationSuccess: () -> Unit) {
    val emailState = remember { mutableStateOf(TextFieldValue()) }
    val passwordState = remember { mutableStateOf(TextFieldValue()) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Регистрация", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(32.dp))

        TextField(value = emailState.value, onValueChange = { emailState.value = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())

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
            modifier = Modifier.clickable { isPasswordVisible = !isPasswordVisible }.padding(end = 8.dp),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Верхняя панель
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { navController.navigate("menu") }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_menu),
                    contentDescription = "Menu"
                )
            }
            Text(
                text = "Главная",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            IconButton(onClick = { navController.navigate("cart") }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_cart),
                    contentDescription = "Cart"
                )
            }
        }

        // Поиск - перемещаем сюда
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 16.dp, end = 16.dp), // Уменьшили отступ сверху
            contentAlignment = Alignment.Center
        ) {
            TextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Поиск") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Search"
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }

        // Категории, Популярное, Акции
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CategoryItem(title = "Категории", onClick = { navController.navigate("categories") })
                CategoryItem(title = "Популярное", onClick = { navController.navigate("popular") })
                CategoryItem(title = "Акции", onClick = { navController.navigate("sales") })
            }
        }

        // Нижнее меню
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigate("home") }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_home),
                    contentDescription = "Home"
                )
            }
            IconButton(onClick = { navController.navigate("favorites") }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_favorites),
                    contentDescription = "Favorites"
                )
            }
            IconButton(onClick = { navController.navigate("notifications") }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_notifications),
                    contentDescription = "Notifications"
                )
            }
            IconButton(onClick = { navController.navigate("profile") }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_profile),
                    contentDescription = "Profile"
                )
            }
        }
    }
}




@Composable
fun CategoryItem(
    title: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
    }
}



@Composable
fun CatalogScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Каталог товаров", style = MaterialTheme.typography.titleLarge)
        // Добавьте список товаров здесь
    }
}

@Composable
fun PopularScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Популярные товары", style = MaterialTheme.typography.titleLarge)

        // Пример карточки товара
        ProductCard(name = "Товар 1", price = "$100") {
            navController.navigate("favorites") // Переход на экран Избранное
        }
    }
}

@Composable
fun ProductCard(name: String, price: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = name, fontWeight = FontWeight.Bold)
            Text(text = price, color = Color.Gray)
        }
    }
}

@Composable
fun FavoritesScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Избранные товары", style = MaterialTheme.typography.titleLarge)
        // Добавьте список избранных товаров здесь
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
