package com.example.pronadjimajstora

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pronadjimajstora.databinding.ActivityHomeCustomerBinding

class HomeCustomerActivity : AppCompatActivity(), FilterDialogFragment.FilterListener {
    private lateinit var binding: ActivityHomeCustomerBinding
    private lateinit var serviceAdapter: ServiceAdapter
    private val mockServices = mutableListOf<Service>()

    // Filter i pretraga varijable
    private var currentCategoryFilter = "Sve kategorije"
    private var currentLocationFilter = ""
    private var currentMaxPriceFilter = 0
    private var currentSearchQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeMockServices()
        setupRecyclerView()
        setupFilterIcon()
        setupProfileButton()
        setupSearchView()
    }

    private fun initializeMockServices() {
        mockServices.clear()
        mockServices.addAll(
            listOf(
                Service(
                    id = "1",
                    name = "Popravak vodovoda",
                    category = "Vodoinstalaterski radovi",
                    craftsman = "Marko Marković",
                    rating = 4.5f,
                    location = "Sarajevo",
                    priceRange = "Po dogovoru",
                    priceRangeMax = 0,
                    description = "Specijalizovani servis za sve vrste vodovodnih intervencija"
                ),
                Service(
                    id = "2",
                    name = "Električarski radovi",
                    category = "Električarski radovi",
                    craftsman = "Ivan Ivanić",
                    rating = 4.8f,
                    location = "Mostar",
                    priceRange = "100-500 KM",
                    priceRangeMax = 500,
                    description = "Instalacije, popravke i modernizacija električnih instalacija"
                )
            )
        )
    }

    private fun setupRecyclerView() {
        // DODAJ LAYOUT MANAGER
        binding.rvServices.layoutManager = LinearLayoutManager(this) // KLJUČNA IZMJENA

        serviceAdapter = ServiceAdapter(mockServices) { service ->
            startActivity(Intent(this, ChatActivity::class.java).apply {
                putExtra("service_id", service.id)
            })
        }
        binding.rvServices.adapter = serviceAdapter
    }

    // Ostale metode ostaju identične
    private fun setupFilterIcon() {
        binding.btnFilter.setOnClickListener {
            FilterDialogFragment().apply {
                setFilterListener(this@HomeCustomerActivity)
            }.show(supportFragmentManager, "FilterDialog")
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

    override fun onFiltersApplied(category: String, location: String, maxPrice: Int) {
        currentCategoryFilter = category
        currentLocationFilter = location.trim()
        currentMaxPriceFilter = maxPrice
        applyAllFilters()
    }

    private fun applyAllFilters() {
        val filteredList = mockServices.filter { service ->
            (currentCategoryFilter == "Sve kategorije" || service.category == currentCategoryFilter) &&
                    (currentLocationFilter.isEmpty() || service.location.contains(currentLocationFilter, true)) &&
                    (currentMaxPriceFilter == 0 || service.priceRangeMax <= currentMaxPriceFilter) &&
                    (service.name.contains(currentSearchQuery, true) ||
                            service.craftsman.contains(currentSearchQuery, true) ||
                            service.category.contains(currentSearchQuery, true) ||
                            service.location.contains(currentSearchQuery, true))
        }

        serviceAdapter.updateList(filteredList)
    }
}