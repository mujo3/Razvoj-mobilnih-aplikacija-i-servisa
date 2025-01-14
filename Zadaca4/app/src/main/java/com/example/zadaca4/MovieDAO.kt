package com.example.zadaca4

import androidx.room.*

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovie(movie: MovieEntity)

    @Query("SELECT * FROM movies WHERE imdbID = :imdbID AND userId = :userId LIMIT 1")
    fun getMovie(imdbID: String, userId: Int): MovieEntity?

    @Query("SELECT * FROM movies WHERE userId = :userId")
    fun getAllMovies(userId: Int): List<MovieEntity>

    @Update
    fun updateMovie(movie: MovieEntity)
}
