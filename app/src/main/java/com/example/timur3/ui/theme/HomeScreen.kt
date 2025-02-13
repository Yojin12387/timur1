package com.example.timur3.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.timur3.R

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween, // Распределяем пространство
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Верхняя часть экрана
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Добро пожаловать в Home!", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Нижнее меню
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { navController.navigate("home") }) {
                Icon(painter = painterResource(id = R.drawable.ic_home), contentDescription = "Home")
            }
            IconButton(onClick = { navController.navigate("catalog") }) {
                Icon(painter = painterResource(id = R.drawable.ic_catalog), contentDescription = "Catalog")
            }
            IconButton(onClick = { navController.navigate("popular") }) {
                Icon(painter = painterResource(id = R.drawable.ic_popular), contentDescription = "Popular")
            }
            IconButton(onClick = { navController.navigate("favorites") }) {
                Icon(painter = painterResource(id = R.drawable.ic_favorites), contentDescription = "Favorites")
            }
        }
    }
}
