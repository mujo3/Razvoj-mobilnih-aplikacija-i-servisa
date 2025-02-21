package com.example.pronadjimajstora

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class LoginViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    companion object {
        private const val KEY_EMAIL = "login_email"
    }


    val email: LiveData<String> = savedStateHandle.getLiveData(KEY_EMAIL, "")

    fun setEmail(newEmail: String) {
        savedStateHandle[KEY_EMAIL] = newEmail
    }
}
