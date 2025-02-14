package com.example.timur3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.timur3.ui.theme.Timur3Theme
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import com.example.timur3.ui.screens.HomeScreen
import com.example.timur3.ui.theme.FavoritesScreen
import com.example.timur3.ui.theme.LoginScreen
import com.example.timur3.ui.theme.OnboardingScreen
import com.example.timur3.ui.theme.RegistrationScreen
import com.example.timur3.ui.theme.SplashScreen
import java.util.UUID


data class Product(
    val id: String = "",
    val name: String = "",
    val price: String = "",
    val imageUrl: String = ""
)
val id = UUID.randomUUID().toString()

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        setContent {
            Timur3Theme {
                val navController = rememberNavController()
                val auth = FirebaseAuth.getInstance() // Получаем экземпляр FirebaseAuth

                NavHost(navController, startDestination = "login") {
                    composable("login") { LoginScreen(auth, navController) }
                    composable("splash") { SplashScreen { navController.navigate("onboard") } }
                    composable("onboard") { OnboardingScreen(onNavigateToHome = { navController.navigate("home") }) }
                    composable("home") { HomeScreen(navController) }
                    composable("registration") {
                        RegistrationScreen(auth = auth) {
                            navController.navigate("login")
                        }
                    }
                    composable("catalog") { CatalogScreen() }
                    composable("favorites") { FavoritesScreen() }
                }
            }
        }

    }
}

@Composable
fun CatalogScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Каталог товаров", style = MaterialTheme.typography.titleLarge)
        // Добавьте список товаров здесь
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


fun loginUser(auth: FirebaseAuth, email: String, password: String, onResult: (Boolean, String) -> Unit) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(true, "Успешный вход")
            } else {
                onResult(false, task.exception?.message ?: "Ошибка входа")
            }
        }
}

fun registerUser(auth: FirebaseAuth, email: String, password: String, onResult: (Boolean, String) -> Unit) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(true, "Успешная регистрация")
            } else {
                onResult(false, task.exception?.message ?: "Ошибка регистрации")
            }
        }
}