package com.example.timur3.ui.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timur3.R

@Composable
fun OnboardingScreen(onNavigateToHome: () -> Unit) {
    var currentScreen by remember { mutableStateOf(0) }
    val progressAlpha1 by animateFloatAsState(targetValue = if (currentScreen >= 0) 1f else 0f, animationSpec = tween(durationMillis = 300))
    val progressAlpha2 by animateFloatAsState(targetValue = if (currentScreen >= 1) 1f else 0f, animationSpec = tween(durationMillis = 300))
    val progressAlpha3 by animateFloatAsState(targetValue = if (currentScreen >= 2) 1f else 0f, animationSpec = tween(durationMillis = 300))

    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = Color(0xFF0077B6))) {
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
                Box(modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = Color.Gray.copy(alpha = progressAlpha1),
                        shape = CircleShape
                    ))
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = Color.Gray.copy(alpha = progressAlpha2),
                        shape = CircleShape
                    ))
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = Color.Gray.copy(alpha = progressAlpha3),
                        shape = CircleShape
                    ))
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF0077B6))
            ) {
                Text(text = if (currentScreen == 2) "Начать" else "Далее", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}