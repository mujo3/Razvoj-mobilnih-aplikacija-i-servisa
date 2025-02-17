package com.example.pronadjimajstora

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pronadjimajstora.databinding.ItemCraftsmanAdBinding

class CraftsmanAdAdapter(
    private var services: List<Service>,
    private val onEditClick: (Service) -> Unit,
    private val onDeleteClick: (Service) -> Unit
) : RecyclerView.Adapter<CraftsmanAdAdapter.AdViewHolder>() {

    inner class AdViewHolder(val binding: ItemCraftsmanAdBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdViewHolder {
        val binding = ItemCraftsmanAdBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AdViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdViewHolder, position: Int) {
        val service = services[position]
        with(holder.binding) {
            tvAdTitle.text = service.name
            tvAdDescription.text = service.description
            // Ažurirana linija: prikaz cijene
            tvAdPrice.text = String.format("%.2f KM", service.price)

            // Učitavanje slike – ako je imageUrl "default", učitava se lokalna slika
            if (service.imageUrl == "default") {
                Glide.with(holder.itemView.context)
                    .load(R.drawable.ic_add_photo)
                    .into(ivAdImage)
            } else {
                Glide.with(holder.itemView.context)
                    .load(service.imageUrl)
                    .placeholder(R.drawable.ic_add_photo)
                    .into(ivAdImage)
            }

            btnEditAd.setOnClickListener { onEditClick(service) }
            btnDeleteAd.setOnClickListener { onDeleteClick(service) }
        }
    }

    override fun getItemCount(): Int = services.size

    fun updateList(newList: List<Service>) {
        services = newList
        notifyDataSetChanged()
    }
}