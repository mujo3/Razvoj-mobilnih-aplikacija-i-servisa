package com.example.pronadjimajstora

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class ProfileViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    companion object {
        private const val KEY_NAME = "PROFILE_NAME"
        private const val KEY_EMAIL = "PROFILE_EMAIL"
        private const val KEY_LOCATION = "PROFILE_LOCATION"
        private const val KEY_PROFILE_PIC_URL = "PROFILE_PIC_URL"
    }

    val name: LiveData<String> =
        savedStateHandle.getLiveData(KEY_NAME, "")
    val email: LiveData<String> =
        savedStateHandle.getLiveData(KEY_EMAIL, "")
    val location: LiveData<String> =
        savedStateHandle.getLiveData(KEY_LOCATION, "")
    val profilePicUrl: LiveData<String> =
        savedStateHandle.getLiveData(KEY_PROFILE_PIC_URL, "")

    fun setName(newName: String) {
        savedStateHandle[KEY_NAME] = newName
    }

    fun setEmail(newEmail: String) {
        savedStateHandle[KEY_EMAIL] = newEmail
    }

    fun setLocation(newLocation: String) {
        savedStateHandle[KEY_LOCATION] = newLocation
    }

    fun setProfilePicUrl(newUrl: String) {
        savedStateHandle[KEY_PROFILE_PIC_URL] = newUrl
    }
}
