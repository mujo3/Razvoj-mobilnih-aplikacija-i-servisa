package com.example.pronadjimajstora

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pronadjimajstora.databinding.ActivityProfileSetupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileSetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSetupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var userType: String = "kupac"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Provera da li je profil već podešen
        checkUserProfile()

        // Postavi podatke dobivene iz prijave
        val email = intent.getStringExtra("email")
        val name = intent.getStringExtra("name")
        binding.etFullName.setText(name ?: "")

        // Promjena tipa korisnika
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

        binding.btnCompleteRegistration.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val location = binding.etLocation.text.toString().trim()
            val specialization = binding.etSpecialization.text.toString().trim()

            if (validateInputs(fullName, location, specialization)) {
                saveUserProfile(fullName, location, specialization)
            }
        }
    }

    private fun checkUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists() && document.getString("name") != null) {
                        // Profil je već podešen, preusmeri na home ekran
                        val userType = document.getString("userType") ?: "kupac"
                        navigateToHome(userType)
                    }
                    // Ako profil nije podešen, nastavi sa inicijalizacijom
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Greška pri provjeri profila: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Korisnik nije prijavljen, preusmeri na LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun validateInputs(
        fullName: String,
        location: String,
        specialization: String
    ): Boolean {
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
