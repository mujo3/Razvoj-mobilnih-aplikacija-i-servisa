package com.example.pronadjimajstora

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.pronadjimajstora.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupUI()
        loadProfileData()
    }

    private fun setupUI() {
        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
    }

    private fun loadProfileData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    document?.let {
                        binding.tvUserName.text = it.getString("name") ?: "Nepoznati korisnik"
                        binding.tvEmail.text = it.getString("email") ?: "email@example.com"
                        binding.tvLocation.text = it.getString("location") ?: "Lokacija nije postavljena"
                        binding.tvFinishedAds.text = "${it.getLong("finished_ads") ?: 0}"
                        binding.tvPostedRequests.text = "${it.getLong("posted_requests") ?: 0}"

                        val profilePicUrl = it.getString("profilePicUrl")
                        if (!profilePicUrl.isNullOrEmpty()) {
                            Glide.with(this).load(profilePicUrl).into(binding.ivProfile)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Greška pri učitavanju podataka: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onResume() {
        super.onResume()
        loadProfileData()
    }
}
