package com.example.pronadjimajstora

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class CraftsmanData(
    var id: String = "",           // Dodano za potrebe dokument referenci
    var name: String = "",         // naziv usluge
    var description: String = "",
    var price: Double = 0.0,
    var craftsman: String = "",     // ime majstora
    var location: String = "",
    var rating: Float = 0f,
    var specialization: String = "",
    var imageUrl: String = "",
    var category: String = ""
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
