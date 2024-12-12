package com.example.pronadjimajstora

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OffersAdapter(private var mList: List<OffersData>) : RecyclerView.Adapter<OffersAdapter.OfferViewHolder>() {

    inner class OfferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logo: ImageView = itemView.findViewById(R.id.illustration)
        val title: TextView = itemView.findViewById(R.id.title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.offers, parent, false)
        return OfferViewHolder(view)
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        holder.logo.setImageResource(mList[position].logo)
        holder.title.text = mList[position].title
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateList(newList: List<OffersData>) {
        mList = newList
        notifyDataSetChanged()
    }


}
