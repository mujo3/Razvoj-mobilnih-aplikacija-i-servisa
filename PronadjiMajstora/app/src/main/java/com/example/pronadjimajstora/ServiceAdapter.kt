package com.example.pronadjimajstora

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pronadjimajstora.databinding.ItemServiceBinding

class ServiceAdapter(
    private var services: List<Service>,
    private val onContactClick: (Service) -> Unit,
    private val onEndAdClick: (Service) -> Unit
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
            ratingBar.rating = service.rating.toFloat()
            tvLocation.text = "Lokacija: ${service.location}"
            tvPriceRange.text = "Cijena: ${service.price}"
            tvDescription.text = service.description

            if (service.imageUrl == "default") {
                Glide.with(holder.itemView.context)
                    .load(R.drawable.ic_add_photo)
                    .into(ivServiceImage)
            } else {
                Glide.with(holder.itemView.context)
                    .load(service.imageUrl)
                    .placeholder(R.drawable.ic_add_photo)
                    .into(ivServiceImage)
            }

            btnContact.setOnClickListener { onContactClick(service) }
            btnEndAd.setOnClickListener { onEndAdClick(service) }
        }
    }

    override fun getItemCount() = services.size

    fun updateList(newList: List<Service>) {
        services = newList
        notifyDataSetChanged()
    }
}
