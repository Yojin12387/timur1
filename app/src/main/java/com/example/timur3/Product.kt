package com.example.timur3.models

data class Product(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val description: String = "",
    val category: String = "",
    val inStock: Boolean = true
) {
    fun formattedPrice(): String {
        return "$${"%.2f".format(price)}" // Форматирование цены
    }
}
