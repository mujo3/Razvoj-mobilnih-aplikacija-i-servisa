package com.example.zadaca4

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.widget.Filter
import android.widget.Filterable

class MovieAdapter(
    private val movies: List<MovieEntity>,
    private val onItemInteraction: (MovieEntity) -> Unit,
    private val onRatingChanged: (MovieEntity, Float) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>(), Filterable {

    private var filteredMovies: List<MovieEntity> = movies

    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTitle)
        val year: TextView = view.findViewById(R.id.tvYear)
        val poster: ImageView = view.findViewById(R.id.ivPoster)
        val ratingBar: RatingBar = view.findViewById(R.id.ratingBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = filteredMovies[position]
        holder.title.text = movie.title ?: "Unknown Title"
        holder.year.text = movie.year ?: "Unknown Year"

        val posterUrl = movie.poster ?: R.drawable.defaultposter
        Glide.with(holder.itemView.context)
            .load(posterUrl)
            .into(holder.poster)

        holder.ratingBar.rating = movie.rating ?: 0f
        holder.itemView.setOnClickListener {
            onItemInteraction(movie)
        }

        holder.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            onRatingChanged(movie, rating)
        }
    }

    override fun getItemCount(): Int {
        return filteredMovies.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                filteredMovies = if (charString.isEmpty()) movies else {
                    movies.filter {
                        it.title?.contains(charString, true) == true ||
                                it.year?.contains(charString, true) == true
                    }
                }
                return FilterResults().apply { values = filteredMovies }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredMovies = results?.values as List<MovieEntity>
                notifyDataSetChanged()
            }
        }
    }
}
