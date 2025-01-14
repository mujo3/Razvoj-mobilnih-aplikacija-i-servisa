package com.example.zadaca4

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var db: AppDatabase
    private var userId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)

        userId = intent.getIntExtra("userId", 0) // Dohvat userId

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        db = AppDatabase.getDatabase(this, lifecycleScope)

        displayMovies()
    }

    private fun getMovies() {
        val call = RetrofitInstance.api.searchMovies("star wars")
        call.enqueue(object : Callback<MovieSearchResponse> {
            override fun onResponse(call: Call<MovieSearchResponse>, response: Response<MovieSearchResponse>) {
                if (response.isSuccessful) {
                    val movies = response.body()?.Search
                    if (movies != null) {
                        val movieEntities = movies.map { movie ->
                            MovieEntity(
                                imdbID = movie.imdbID ?: "",
                                title = movie.Title,
                                year = movie.Year,
                                poster = movie.Poster,
                                rating = 0f,
                                userId = userId,
                                isFavorite = false,
                                notes = null
                            )
                        }
                        lifecycleScope.launch(Dispatchers.IO) {
                            movieEntities.forEach { db.movieDao().insertMovie(it) }
                            withContext(Dispatchers.Main) {
                                displayMovies()
                            }
                        }
                    } else {
                        Toast.makeText(this@MovieListActivity, "No movies found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MovieListActivity, "Failed to retrieve movies", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MovieSearchResponse>, t: Throwable) {
                Toast.makeText(this@MovieListActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayMovies() {
        lifecycleScope.launch(Dispatchers.IO) {
            val movies = db.movieDao().getAllMovies(userId)
            withContext(Dispatchers.Main) {
                if (movies.isEmpty()) {
                    getMovies()
                } else {
                    movieAdapter = MovieAdapter(movies,
                        onItemInteraction = { movie ->
                            // Handle item interaction
                        },
                        onRatingChanged = { movie, rating ->
                            lifecycleScope.launch(Dispatchers.IO) {
                                val updatedMovie = movie.copy(rating = rating)
                                db.movieDao().updateMovie(updatedMovie)
                                Log.d("Database", "Updated movie: ${updatedMovie.title}, Rating: ${updatedMovie.rating}, UserId: ${updatedMovie.userId}")
                            }
                        }
                    )
                    recyclerView.adapter = movieAdapter
                }
            }
        }
    }
}
