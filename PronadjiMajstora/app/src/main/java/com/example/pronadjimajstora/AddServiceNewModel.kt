package com.example.pronadjimajstora

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class AddServiceViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    companion object {
        private const val KEY_TITLE = "add_service_title"
        private const val KEY_DESCRIPTION = "add_service_description"
        private const val KEY_PRICE = "add_service_price"
        private const val KEY_IMAGE_URL = "add_service_image_url"
    }

    val title: LiveData<String> = savedStateHandle.getLiveData(KEY_TITLE, "")
    val description: LiveData<String> = savedStateHandle.getLiveData(KEY_DESCRIPTION, "")
    val price: LiveData<String> = savedStateHandle.getLiveData(KEY_PRICE, "")
    val imageUrl: LiveData<String> = savedStateHandle.getLiveData(KEY_IMAGE_URL, "default")

    fun setTitle(value: String) {
        savedStateHandle[KEY_TITLE] = value
    }

    fun setDescription(value: String) {
        savedStateHandle[KEY_DESCRIPTION] = value
    }

    fun setPrice(value: String) {
        savedStateHandle[KEY_PRICE] = value
    }

    fun setImageUrl(value: String) {
        savedStateHandle[KEY_IMAGE_URL] = value
    }
}
