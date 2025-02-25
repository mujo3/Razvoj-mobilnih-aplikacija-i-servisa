package com.example.zadaca4

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/")
    fun searchMovies(
        @Query("s") query: String,
        @Query("apikey") apiKey: String = "aced1c2"
    ): Call<MovieSearchResponse>
}