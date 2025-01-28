package com.example.pronadjimajstora

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pronadjimajstora.databinding.ItemServiceBinding

class ServiceAdapter(
    private var services: List<Service>, // Promijenjeno u var za update
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
            tvCraftsmanName.text = service.craftsman // Ispravka: prikaz imena umjesto ID-a
            ratingBar.rating = service.rating // Realna ocjena umjesto mock podatka
            tvLocation.text = "Lokacija: ${service.location}"
            tvPriceRange.text = "Cijena: ${service.priceRange}" // Dodat prikaz cijene
            tvDescription.text = service.description

            btnContact.setOnClickListener { onContactClick(service) }
        }
    }

    override fun getItemCount() = services.size

    // Funkcija za ažuriranje liste
    fun updateList(newList: List<Service>) {
        services = newList
        notifyDataSetChanged()
    }
}