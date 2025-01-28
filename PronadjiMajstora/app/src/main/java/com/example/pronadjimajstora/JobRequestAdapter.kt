package com.example.pronadjimajstora

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pronadjimajstora.databinding.ItemJobRequestBinding

class JobRequestAdapter(
    private val requests: List<JobRequest>
) : RecyclerView.Adapter<JobRequestAdapter.JobRequestViewHolder>() {

    inner class JobRequestViewHolder(val binding: ItemJobRequestBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobRequestViewHolder {
        val binding = ItemJobRequestBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return JobRequestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobRequestViewHolder, position: Int) {
        val request = requests[position]
        with(holder.binding) {
            tvDescription.text = request.description
            tvBudget.text = "Budžet: ${request.budget} KM"
            tvStatus.text = "Status: ${request.status}"
        }
    }

    override fun getItemCount() = requests.size
}