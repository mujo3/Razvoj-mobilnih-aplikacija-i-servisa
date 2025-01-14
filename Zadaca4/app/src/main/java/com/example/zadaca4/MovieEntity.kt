package com.example.zadaca4

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val imdbID: String,
    val title: String?,
    val year: String?,
    val poster: String?,
    val rating: Float? = 0f,
    val userId: Int,
    val isFavorite: Boolean = false,
    val notes: String? = null
)
