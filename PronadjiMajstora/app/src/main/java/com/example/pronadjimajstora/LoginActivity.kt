package com.example.pronadjimajstora

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pronadjimajstora.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (validateInput(email, password)) {
                // Direktno preusmjeri na Home ekran
                navigateToHome()
            }
        }

        binding.tvRegister.setOnClickListener {
            navigateToRegister()
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        return when {
            email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.etEmail.error = "Unesite validan email"
                false
            }
            password.isEmpty() || password.length < 6 -> {
                binding.etPassword.error = "Šifra mora imati najmanje 6 znakova"
                false
            }
            else -> true
        }
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToHome() {
        // Pretpostavimo da je korisnik "kupac" za testiranje
        val intent = Intent(this, HomeCustomerActivity::class.java)
        startActivity(intent)
        finish()
    }
}