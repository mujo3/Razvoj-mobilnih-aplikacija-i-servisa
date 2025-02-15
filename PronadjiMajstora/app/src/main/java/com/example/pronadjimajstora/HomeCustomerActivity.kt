package com.example.pronadjimajstora

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pronadjimajstora.databinding.ActivityHomeCustomerBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeCustomerActivity : AppCompatActivity(), FilterDialogFragment.FilterListener {
    private lateinit var binding: ActivityHomeCustomerBinding
    private lateinit var serviceAdapter: ServiceAdapter
    private var serviceList = mutableListOf<Service>()
    private val db: FirebaseFirestore = Firebase.firestore

    // Filter i pretraga varijable
    private var currentCategoryFilter = "Sve kategorije"
    private var currentLocationFilter = ""
    private var currentMaxPriceFilter = 5000.0
    private var currentSearchQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupFilterIcon()
        setupProfileButton()
        setupSearchView()
        fetchServicesFromFirestore()
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    private fun setupRecyclerView() {
        binding.rvServices.layoutManager = LinearLayoutManager(this)
        serviceAdapter = ServiceAdapter(serviceList) { service ->
            startActivity(Intent(this, ChatActivity::class.java).apply {
                putExtra("service_id", service.id)
            })
        }
        binding.rvServices.adapter = serviceAdapter
    }

    private fun fetchServicesFromFirestore() {
        db.collection("services")
            .get()
            .addOnSuccessListener { querySnapshot ->
                serviceList = querySnapshot.documents.mapNotNull { document ->
                    try {
                        val service = document.toObject(Service::class.java)
                        service?.copy(id = document.id)
                    } catch (e: Exception) {
                        Log.e("Firestore", "Error parsing document ${document.id}: ${e.message}")
                        null
                    }
                }.toMutableList()

                Log.d("Firestore", "Fetched ${serviceList.size} services")
                applyAllFilters()
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching services: ${exception.message}")
                Toast.makeText(this, "Greška pri učitavanju servisa", Toast.LENGTH_LONG).show()
            }
    }

    private fun setupFilterIcon() {
        binding.btnFilter.setOnClickListener {
            FilterDialogFragment().apply {
                setFilterListener(this@HomeCustomerActivity)
                show(supportFragmentManager, "FilterDialog")
            }
        }
    }

    private fun setupProfileButton() {
        binding.fabProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                currentSearchQuery = newText?.trim().orEmpty()
                applyAllFilters()
                return true
            }
        })
    }

    override fun onFiltersApplied(category: String, location: String, maxPrice: Double) {
        currentCategoryFilter = category
        currentLocationFilter = location.trim()
        currentMaxPriceFilter = if (maxPrice == 0.0) 5000.0 else maxPrice
        applyAllFilters()
    }

    private fun applyAllFilters() {
        val filteredList = serviceList.filter { service ->
            (currentCategoryFilter == "Sve kategorije" || service.specialization.equals(currentCategoryFilter, true)) &&
                    (currentLocationFilter.isEmpty() || service.location.contains(currentLocationFilter, true)) &&
                    (service.price <= currentMaxPriceFilter) &&
                    (currentSearchQuery.isEmpty() ||
                            service.name.contains(currentSearchQuery, true) ||
                            service.location.contains(currentSearchQuery, true) ||
                            service.specialization.contains(currentSearchQuery, true))
        }

        serviceAdapter.updateList(filteredList)

        if (filteredList.isEmpty()) {
            binding.rvServices.visibility = View.GONE
            binding.tvEmptyState.visibility = View.VISIBLE
        } else {
            binding.rvServices.visibility = View.VISIBLE
            binding.tvEmptyState.visibility = View.GONE
        }
    }
}
