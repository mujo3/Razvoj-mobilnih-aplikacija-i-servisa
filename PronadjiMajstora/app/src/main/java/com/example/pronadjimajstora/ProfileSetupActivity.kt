package com.example.pronadjimajstora

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.pronadjimajstora.databinding.ActivityProfileSetupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileSetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSetupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var userType: String = "kupac"

    // Koristimo ProfileSetupViewModel za čuvanje unosa
    private val profileSetupViewModel: ProfileSetupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        checkUserProfile()

        // Postavljanje podataka iz intent-a i ViewModel-a
        val email = intent.getStringExtra("email")
        val name = intent.getStringExtra("name")
        if (profileSetupViewModel.fullName.value.isNullOrEmpty()) {
            profileSetupViewModel.setFullName(name ?: "")
        }
        binding.etFullName.setText(profileSetupViewModel.fullName.value)
        binding.etLocation.setText(profileSetupViewModel.location.value)
        binding.etSpecialization.setText(profileSetupViewModel.specialization.value)

        binding.radioUserType.setOnCheckedChangeListener { _, checkedId ->
            userType = when (checkedId) {
                R.id.radioCustomer -> {
                    binding.layoutSpecialization.visibility = View.GONE
                    "kupac"
                }
                R.id.radioCraftsman -> {
                    binding.layoutSpecialization.visibility = View.VISIBLE
                    "majstor"
                }
                else -> "kupac"
            }
        }

        setupTextWatchers()

        binding.btnCompleteRegistration.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val location = binding.etLocation.text.toString().trim()
            val specialization = binding.etSpecialization.text.toString().trim()

            if (validateInputs(fullName, location, specialization)) {
                saveUserProfile(fullName, location, specialization)
            }
        }
    }

    private fun setupTextWatchers() {
        binding.etFullName.addTextChangedListener(SimpleTextWatcher { profileSetupViewModel.setFullName(it) })
        binding.etLocation.addTextChangedListener(SimpleTextWatcher { profileSetupViewModel.setLocation(it) })
        binding.etSpecialization.addTextChangedListener(SimpleTextWatcher { profileSetupViewModel.setSpecialization(it) })
    }

    private fun checkUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists() && document.getString("name") != null) {
                        val userType = document.getString("userType") ?: "kupac"
                        navigateToHome(userType)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Greška pri provjeri profila: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun validateInputs(fullName: String, location: String, specialization: String): Boolean {
        return when {
            fullName.isEmpty() -> {
                binding.etFullName.error = "Unesite ime i prezime"
                false
            }
            location.isEmpty() -> {
                binding.etLocation.error = "Unesite lokaciju"
                false
            }
            (userType == "majstor" && specialization.isEmpty()) -> {
                binding.etSpecialization.error = "Unesite specijalizaciju"
                false
            }
            else -> true
        }
    }

    private fun saveUserProfile(fullName: String, location: String, specialization: String) {
        val uid = auth.currentUser?.uid ?: return
        val currentUser = auth.currentUser

        val userData = hashMapOf<String, Any>(
            "email" to (currentUser?.email ?: ""),
            "userType" to userType,
            "name" to fullName,
            "location" to location,
            "profilePicUrl" to (currentUser?.photoUrl?.toString() ?: ""),
            "specialization" to (if (userType == "majstor") specialization else ""),
            "phone" to "",
            "bio" to "",
            "rating" to 0.0,
            "finished_ads" to 0,
            "posted_requests" to 0
        )

        firestore.collection("users").document(uid)
            .set(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "Profil uspješno kreiran", Toast.LENGTH_SHORT).show()
                navigateToHome(userType)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Greška pri spremanju profila: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToHome(userType: String) {
        val intent = if (userType == "majstor") {
            Intent(this, HomeCraftsmanActivity::class.java)
        } else {
            Intent(this, HomeCustomerActivity::class.java)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
