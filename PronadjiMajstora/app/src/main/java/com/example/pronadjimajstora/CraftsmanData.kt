package com.example.pronadjimajstora

data class CraftsmanData(
    val name: String,           // naziv usluge (service name)
    val description: String,
    val price: Double,
    val craftsman: String,       // ime majstora
    val location: String,
    val rating: Float,
    val specialization: String,
    val imageUrl: String,
    val category: String = ""
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "description" to description,
            "price" to price,
            "craftsman" to craftsman,
            "location" to location,
            "rating" to rating,
            "specialization" to specialization,
            "imageUrl" to imageUrl,
            "category" to category
        )
    }
}
