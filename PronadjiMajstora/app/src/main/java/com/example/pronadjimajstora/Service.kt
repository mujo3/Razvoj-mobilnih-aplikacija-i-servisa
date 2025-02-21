package com.example.pronadjimajstora

data class Service(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val craftsman: String = "",
    val rating: Float = 0f,
    val numRatings: Long = 0L,
    val location: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val specialization: String = "",
    val imageUrl: String = "",
    val ratedBy: List<String> = emptyList()
)
