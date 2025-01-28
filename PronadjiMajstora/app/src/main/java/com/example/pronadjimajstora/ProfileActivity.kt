package com.example.pronadjimajstora

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pronadjimajstora.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadMockData() // Učitaj testne podatke
    }

    private fun loadMockData() {
        // Testni podaci (zamijeniti kasnije sa pravim podacima)
        binding.tvUserName.text = "Marko Marković"
        binding.tvEmail.text = "marko@example.com"
        binding.tvUserType.text = "kupac"
    }
}