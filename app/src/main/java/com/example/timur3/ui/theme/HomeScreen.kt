package com.example.timur3.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.timur3.Product
import com.example.timur3.R
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

@Composable
fun HomeScreen(navController: NavController) {
    var products by remember { mutableStateOf(listOf<Product>()) }
    var searchQuery by remember { mutableStateOf("") }
    var favoriteProducts by remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(Unit) {
        fetchProducts { fetchedProducts ->
            products = fetchedProducts
            fetchFavoriteProducts { favorites ->
                favoriteProducts = favorites
            }
        }
    }

    val filteredProducts = products.filter { product ->
        product.name.contains(searchQuery, ignoreCase = true)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 70.dp) // Оставляем место для нижнего меню
        ) {
            TopAppBar(navController)
            SearchField(searchQuery) { searchQuery = it }
            ProductList(filteredProducts, favoriteProducts) { productId ->
                favoriteProducts = if (favoriteProducts.contains(productId)) {
                    favoriteProducts - productId
                } else {
                    favoriteProducts + productId
                }
                updateFavoriteStatus(productId, !favoriteProducts.contains(productId))
            }
        }

        BottomNavigationBar(navController)
    }
}

@Composable
fun TopAppBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = { navController.navigate("menu") }) {
            Icon(painter = painterResource(id = R.drawable.ic_menu), contentDescription = "Menu")
        }
        Text(
            text = "Главная",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        IconButton(onClick = { navController.navigate("cart") }) {
            Icon(painter = painterResource(id = R.drawable.ic_cart), contentDescription = "Cart")
        }
    }
}

@Composable
fun SearchField(searchQuery: String, onSearchQueryChange: (String) -> Unit) {
    TextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        label = { Text("Поиск товаров") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}


@Composable
fun ProductList(
    filteredProducts: List<Product>,
    favoriteProducts: Set<String>,
    onFavoriteClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filteredProducts) { product ->
            ProductCard(
                product = product,
                isFavorite = product.id in favoriteProducts,
                onClick = { /* Действие при нажатии на товар */ },
                onFavoriteClick = { onFavoriteClick(product.id) }
            )
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .fillMaxHeight() // Заполняем всю высоту
    ) {
        Spacer(modifier = Modifier.weight(1f)) // Заполняем доступное пространство

        Row(
            modifier = Modifier.fillMaxWidth(),  // Выравниваем Row по центру горизонтально
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigate("home") }) {
                Icon(painter = painterResource(id = R.drawable.ic_home), contentDescription = "Главная")
            }
            IconButton(onClick = { navController.navigate("catalog") }) {
                Icon(painter = painterResource(id = R.drawable.ic_catalog), contentDescription = "Каталог")
            }
            IconButton(onClick = { navController.navigate("favorites") }) {
                Icon(painter = painterResource(id = R.drawable.ic_favorites), contentDescription = "Избранное")
            }
            IconButton(onClick = { navController.navigate("profile") }) {
                Icon(painter = painterResource(id = R.drawable.ic_profile), contentDescription = "Профиль")
            }
        }
    }
}

fun addProduct(product: Product, onResult: (Boolean) -> Unit) {
    val database = FirebaseDatabase.getInstance().reference
    val newProductRef = database.child("products").push()

    newProductRef.setValue(product).addOnSuccessListener {
        onResult(true)
    }.addOnFailureListener {
        onResult(false)
    }
}

fun fetchProducts(onResult: (List<Product>) -> Unit) {
    val database = FirebaseDatabase.getInstance().reference
    database.child("products").get().addOnSuccessListener { snapshot ->
        val products = mutableListOf<Product>()
        for (child in snapshot.children) {
            val product = child.getValue(Product::class.java)
            if (product != null) {
                products.add(product)
            }
        }
        onResult(products)
    }.addOnFailureListener {
        onResult(emptyList())
    }
}

@Composable
fun ProductCard(
    product: Product,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberImagePainter(product.imageUrl),
                contentDescription = product.name,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.name, style = MaterialTheme.typography.bodyLarge)
                Text(text = product.price, style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = { onFavoriteClick(product.id) }) {
                Icon(
                    painter = painterResource(id = if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_outline),
                    contentDescription = if (isFavorite) "Удалить из избранного" else "Добавить в избранное",
                    tint = if (isFavorite) Color.Red else Color.Gray
                )
            }
        }
    }
}

// Функция для обновления статуса избранного продукта
fun updateFavoriteStatus(productId: String, isFavorite: Boolean) {
    val database = FirebaseDatabase.getInstance().reference
    val favoritesRef = database.child("favorites").child(productId)

    if (isFavorite) {
        favoritesRef.setValue(true).addOnSuccessListener {
            Log.d("Favorites", "Продукт добавлен в избранное: $productId")
        }.addOnFailureListener {
            Log.e("Favorites", "Ошибка добавления в избранное: ${it.message}")
        }
    } else {
        favoritesRef.removeValue().addOnSuccessListener {
            Log.d("Favorites", "Продукт удален из избранного: $productId")
        }.addOnFailureListener {
            Log.e("Favorites", "Ошибка удаления из избранного: ${it.message}")
        }
    }
}

// Функция для получения избранных продуктов
fun fetchFavoriteProducts(onResult: (Set<String>) -> Unit) {
    val database = FirebaseDatabase.getInstance().reference
    database.child("favorites").get().addOnSuccessListener { snapshot ->
        val favorites = mutableSetOf<String>()
        for (child in snapshot.children) {
            favorites.add(child.key ?: "")
        }
        onResult(favorites)
    }.addOnFailureListener {
        onResult(emptySet())
    }
}
