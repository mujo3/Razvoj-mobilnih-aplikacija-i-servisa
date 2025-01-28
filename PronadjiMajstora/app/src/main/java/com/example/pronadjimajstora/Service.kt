package com.example.pronadjimajstora

data class Service(
    val id: String,
    val name: String,
    val category: String,  // Dodat atribut za kategoriju
    val craftsman: String,
    val rating: Float,
    val location: String,
    val priceRange: String,  // Tekstualni prikaz cijene
    val priceRangeMax: Int,  // Numerička maksimalna cijena za filtriranje
    val description: String
)