package com.example.pronadjimajstora

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class FilterViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    companion object {
        private const val KEY_CATEGORY = "FILTER_CATEGORY"
        private const val KEY_LOCATION = "FILTER_LOCATION"
        private const val KEY_MAX_PRICE = "FILTER_MAX_PRICE"
        private const val KEY_SEARCH_QUERY = "SEARCH_QUERY"
    }

    val category: LiveData<String> =
        savedStateHandle.getLiveData(KEY_CATEGORY, "Sve kategorije")
    val location: LiveData<String> =
        savedStateHandle.getLiveData(KEY_LOCATION, "")
    val maxPrice: LiveData<Int> =
        savedStateHandle.getLiveData(KEY_MAX_PRICE, 5000)
    val searchQuery: LiveData<String> =
        savedStateHandle.getLiveData(KEY_SEARCH_QUERY, "")

    fun setCategory(newCategory: String) {
        savedStateHandle[KEY_CATEGORY] = newCategory
    }

    fun setLocation(newLocation: String) {
        savedStateHandle[KEY_LOCATION] = newLocation
    }

    fun setMaxPrice(newMaxPrice: Int) {
        savedStateHandle[KEY_MAX_PRICE] = newMaxPrice
    }

    fun setSearchQuery(newQuery: String) {
        savedStateHandle[KEY_SEARCH_QUERY] = newQuery
    }
}
