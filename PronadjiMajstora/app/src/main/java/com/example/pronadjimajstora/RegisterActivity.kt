package com.example.pronadjimajstora

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pronadjimajstora.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.btnRegister.setOnClickListener {
            val name = binding.etNameSurname.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            val userType = getSelectedUserType()

            if (validateInputs(name, email, password, confirmPassword, userType)) {
                registerUser(name, email, password, userType)
            }
        }

        binding.btnGoogle.setOnClickListener {
            Toast.makeText(this, "Google prijava trenutno nije dostupna", Toast.LENGTH_SHORT).show()
        }

        binding.btnFacebook.setOnClickListener {
            Toast.makeText(this, "Facebook prijava trenutno nije dostupna", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSelectedUserType(): String {
        return when (binding.radioUserType.checkedRadioButtonId) {
            R.id.radioCustomer -> "kupac"
            R.id.radioCraftsman -> "majstor"
            else -> ""
        }
    }

    private fun validateInputs(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        userType: String
    ): Boolean {
        return when {
            name.isEmpty() -> {
                binding.etNameSurname.error = "Unesite ime i prezime"
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.etEmail.error = "Nevalidan email"
                false
            }
            password.length < 6 -> {
                binding.etPassword.error = "Šifra mora imati najmanje 6 znakova"
                false
            }
            password != confirmPassword -> {
                binding.etConfirmPassword.error = "Šifre se ne podudaraju"
                false
            }
            userType.isEmpty() -> {
                Toast.makeText(this, "Odaberite tip korisnika", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun registerUser(name: String, email: String, password: String, userType: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""
                    val userData = hashMapOf(
                        "email" to email,
                        "userType" to userType,
                        "name" to name,
                        "location" to "",
                        "profilePicUrl" to "",
                        "specialization" to "",
                        "phone" to "",
                        "bio" to "",
                        "rating" to 0.0,
                        "finished_ads" to 0,
                        "posted_requests" to 0
                    )
                    firestore.collection("users").document(uid)
                        .set(userData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Registracija uspješna", Toast.LENGTH_SHORT).show()
                            navigateToHome(userType)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Greška pri spremanju podataka: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Registracija neuspješna: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToHome(userType: String) {
        val intent = if (userType == "majstor") {
            Intent(this, HomeCraftsmanActivity::class.java)
        } else {
            Intent(this, HomeCustomerActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

    companion object {
        const val RC_GOOGLE_SIGN_IN = 1001
    }
}