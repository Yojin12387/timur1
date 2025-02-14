package com.example.timur3.ui.theme

import com.google.firebase.database.FirebaseDatabase

class DatabaseHelper {

    // DatabaseHelper.kt или FirebaseRepository.kt

    fun addFavorite(productId: String, onResult: (Boolean) -> Unit) {
        val database = FirebaseDatabase.getInstance().reference
        val favoritesRef = database.child("favorites").child(productId)

        favoritesRef.setValue(true).addOnSuccessListener {
            onResult(true)
        }.addOnFailureListener {
            onResult(false)
        }
    }

    fun removeFavorite(productId: String, onResult: (Boolean) -> Unit) {
        val database = FirebaseDatabase.getInstance().reference
        val favoritesRef = database.child("favorites").child(productId)

        favoritesRef.removeValue().addOnSuccessListener {
            onResult(true)
        }.addOnFailureListener {
            onResult(false)
        }
    }

    fun fetchFavoriteProducts(onResult: (List<String>) -> Unit) {
        val database = FirebaseDatabase.getInstance().reference
        database.child("favorites").get().addOnSuccessListener { snapshot ->
            val favorites = snapshot.children.map { it.key ?: "" }
            onResult(favorites)
        }.addOnFailureListener {
            onResult(emptyList())
        }
    }

}