package com.example.pronadjimajstora

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pronadjimajstora.databinding.ActivityHomeCustomerBinding
import com.google.firebase.firestore.FirebaseFirestore

class HomeCustomerActivity : AppCompatActivity(), FilterDialogFragment.FilterListener {
    private lateinit var binding: ActivityHomeCustomerBinding
    private lateinit var serviceAdapter: ServiceAdapter
    private var serviceList = mutableListOf<Service>()

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
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("services")
            .get()
            .addOnSuccessListener { querySnapshot ->
                serviceList.clear()
                for (document in querySnapshot.documents) {
                    val service = document.toObject(Service::class.java)
                    service?.let { serviceList.add(it) }
                }
                applyAllFilters()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Greška pri učitavanju servisa: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun setupFilterIcon() {
        binding.btnFilter.setOnClickListener {
            val filterDialog = FilterDialogFragment()
            filterDialog.setFilterListener(this)
            filterDialog.show(supportFragmentManager, "FilterDialog")
        }
    }

    private fun setupProfileButton() {
        binding.fabProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
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
                    (currentSearchQuery.isEmpty() || service.name.contains(currentSearchQuery, true) ||
                            service.craftsman.contains(currentSearchQuery, true) ||
                            service.specialization.contains(currentSearchQuery, true) ||
                            service.location.contains(currentSearchQuery, true))
        }
        serviceAdapter.updateList(filteredList)
    }
}
