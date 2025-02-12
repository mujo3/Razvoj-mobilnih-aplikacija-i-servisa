package com.example.pronadjimajstora

data class Service(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val craftsman: String = "",
    val rating: Float = 0f,
    val location: String = "",
    val priceRange: String = "",
    val priceRangeMax: Double = 0.0,
    val description: String = "",
    val specialization: String = "",
    val imageUrl: String = ""
)
