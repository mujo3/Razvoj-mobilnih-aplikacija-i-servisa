package com.example.pronadjimajstora

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class ProfileSetupViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    companion object {
        private const val KEY_FULL_NAME = "profile_full_name"
        private const val KEY_LOCATION = "profile_location"
        private const val KEY_SPECIALIZATION = "profile_specialization"
    }

    val fullName: LiveData<String> = savedStateHandle.getLiveData(KEY_FULL_NAME, "")
    val location: LiveData<String> = savedStateHandle.getLiveData(KEY_LOCATION, "")
    val specialization: LiveData<String> = savedStateHandle.getLiveData(KEY_SPECIALIZATION, "")

    fun setFullName(name: String) {
        savedStateHandle[KEY_FULL_NAME] = name
    }

    fun setLocation(loc: String) {
        savedStateHandle[KEY_LOCATION] = loc
    }

    fun setSpecialization(spec: String) {
        savedStateHandle[KEY_SPECIALIZATION] = spec
    }
}
