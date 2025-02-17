package com.example.pronadjimajstora

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class CraftsmanProfileViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    companion object {
        private const val KEY_NAME = "craftsman_name"
        private const val KEY_SPECIALIZATION = "craftsman_specialization"
        private const val KEY_LOCATION = "craftsman_location"
        private const val KEY_PHONE = "craftsman_phone"
        private const val KEY_EMAIL = "craftsman_email"
        private const val KEY_BIO = "craftsman_bio"
        private const val KEY_PROFILE_PIC_URL = "craftsman_profile_pic_url"
        private const val KEY_RATING = "craftsman_rating"
    }

    val name: LiveData<String> = savedStateHandle.getLiveData(KEY_NAME, "")
    val specialization: LiveData<String> = savedStateHandle.getLiveData(KEY_SPECIALIZATION, "")
    val location: LiveData<String> = savedStateHandle.getLiveData(KEY_LOCATION, "")
    val phone: LiveData<String> = savedStateHandle.getLiveData(KEY_PHONE, "")
    val email: LiveData<String> = savedStateHandle.getLiveData(KEY_EMAIL, "")
    val bio: LiveData<String> = savedStateHandle.getLiveData(KEY_BIO, "")
    val profilePicUrl: LiveData<String> = savedStateHandle.getLiveData(KEY_PROFILE_PIC_URL, "")
    val rating: LiveData<Float> = savedStateHandle.getLiveData(KEY_RATING, 0f)

    fun setName(value: String) {
        savedStateHandle[KEY_NAME] = value
    }

    fun setSpecialization(value: String) {
        savedStateHandle[KEY_SPECIALIZATION] = value
    }

    fun setLocation(value: String) {
        savedStateHandle[KEY_LOCATION] = value
    }

    fun setPhone(value: String) {
        savedStateHandle[KEY_PHONE] = value
    }

    fun setEmail(value: String) {
        savedStateHandle[KEY_EMAIL] = value
    }

    fun setBio(value: String) {
        savedStateHandle[KEY_BIO] = value
    }

    fun setProfilePicUrl(value: String) {
        savedStateHandle[KEY_PROFILE_PIC_URL] = value
    }

    fun setRating(value: Float) {
        savedStateHandle[KEY_RATING] = value
    }
}
