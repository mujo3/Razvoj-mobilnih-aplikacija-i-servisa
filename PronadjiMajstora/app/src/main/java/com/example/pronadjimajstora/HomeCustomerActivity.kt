package com.example.pronadjimajstora

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
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

    // Koristimo FilterViewModel za dijeljenje stanja (filteri i search query)
    private val filterViewModel: FilterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupFilterIcon()
        setupProfileButton()
        setupSearchView()
        fetchServicesFromFirestore()

        // Promatraj promjene u filterima i search queryju – primjeni filtriranje kad god se promijene
        filterViewModel.category.observe(this) { applyAllFilters() }
        filterViewModel.location.observe(this) { applyAllFilters() }
        filterViewModel.maxPrice.observe(this) { applyAllFilters() }
        filterViewModel.searchQuery.observe(this) { applyAllFilters() }
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
                filterViewModel.setSearchQuery(newText?.trim().orEmpty())
                return true
            }
        })
        val searchEditText =
            binding.searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(ContextCompat.getColor(this, android.R.color.black))
        searchEditText.setHintTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
    }

    override fun onFiltersApplied(category: String, location: String, maxPrice: Double) {
        // Ažuriramo ViewModel – observeri će automatski pozvati applyAllFilters()
        filterViewModel.setCategory(category)
        filterViewModel.setLocation(location.trim())
        filterViewModel.setMaxPrice(maxPrice.toInt())
    }

    private fun applyAllFilters() {
        val currentCategory = filterViewModel.category.value ?: "Sve kategorije"
        val currentLocation = filterViewModel.location.value ?: ""
        val currentMaxPrice = filterViewModel.maxPrice.value ?: 5000
        val currentSearchQuery = filterViewModel.searchQuery.value ?: ""

        val filteredList = serviceList.filter { service ->
            (currentCategory == "Sve kategorije" || service.specialization.equals(currentCategory, true)) &&
                    (currentLocation.isEmpty() || service.location.contains(currentLocation, true)) &&
                    (service.price <= currentMaxPrice) &&
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
