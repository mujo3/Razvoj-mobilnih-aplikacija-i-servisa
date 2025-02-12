package com.example.pronadjimajstora

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pronadjimajstora.databinding.ItemServiceBinding

class ServiceAdapter(
    private var services: List<Service>,
    private val onContactClick: (Service) -> Unit
) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    inner class ServiceViewHolder(val binding: ItemServiceBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val binding = ItemServiceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ServiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = services[position]
        with(holder.binding) {
            tvServiceName.text = service.name
            tvCraftsmanName.text = service.craftsman
            ratingBar.rating = service.rating
            tvLocation.text = "Lokacija: ${service.location}"
            tvPriceRange.text = "Cijena: ${service.priceRange}"
            tvDescription.text = service.description

            // Ako je imageUrl marker "default", učitaj lokalni resurs; inače učitaj URL slike
            if (service.imageUrl == "default") {
                Glide.with(holder.itemView.context)
                    .load(R.drawable.ic_add_photo) // lokalni resurs default slike
                    .into(ivServiceImage)
            } else {
                Glide.with(holder.itemView.context)
                    .load(service.imageUrl)
                    .placeholder(R.drawable.ic_add_photo)
                    .into(ivServiceImage)
            }

            btnContact.setOnClickListener { onContactClick(service) }
        }
    }

    override fun getItemCount() = services.size

    // Metoda za ažuriranje liste servisa
    fun updateList(newList: List<Service>) {
        services = newList
        notifyDataSetChanged()
    }
}
