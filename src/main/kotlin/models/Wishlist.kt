package org.example.models

data class Wishlist(
    val description: String,
    val name: String = description.first().toString()
)