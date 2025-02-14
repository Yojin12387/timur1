package com.example.timur3.ui.theme

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.example.timur3.models.Product
import coil.compose.rememberImagePainter // Для загрузки изображений

@Composable
fun FavoritesScreen() {
    var favoriteProducts by remember { mutableStateOf<List<Product>>(emptyList()) }

    LaunchedEffect(Unit) {
        getFavoriteProducts { products ->
            favoriteProducts = products
        }
    }




if (favoriteProducts.isEmpty()) {
            Text("Нет избранных продуктов", modifier = Modifier.padding(16.dp))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {
                items(favoriteProducts) { product ->
                    FavoriteProductItem(product = product)
                }
            }
        }
    }

@Composable
fun FavoriteProductItem(product: Product) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val painter: Painter = rememberImagePainter(product.imageUrl)
        Image(
            painter = painter,
            contentDescription = product.name,
            modifier = Modifier
                .size(64.dp)
                .padding(end = 8.dp)
        )
        Column {
            Text(text = product.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = "${product.price} ₽", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// Функция для получения избранных продуктов из Firebase
fun getFavoriteProducts(onResult: (List<Product>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("favorites")
        .get()
        .addOnSuccessListener { documents ->
            val products = documents.mapNotNull { document ->
                document.toObject(Product::class.java)
            }
            Log.d("FavoritesScreen", "Полученные продукты: $products") // Лог полученных продуктов
            onResult(products)
        }
        .addOnFailureListener { exception ->
            Log.e("FavoritesScreen", "Ошибка получения продуктов: ${exception.message}") // Лог ошибки
            onResult(emptyList()) // Возвращаем пустой список в случае ошибки
        }
}
