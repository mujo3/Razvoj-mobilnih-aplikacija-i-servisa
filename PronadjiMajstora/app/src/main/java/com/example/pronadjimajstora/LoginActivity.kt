package com.example.pronadjimajstora

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.pronadjimajstora.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    // Inicijaliziramo LoginViewModel – čuva se samo email
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        setupClickListeners()
        setupTextWatchers()
        // Vraćamo spremljeni email iz ViewModel-a
        binding.etEmail.setText(loginViewModel.email.value)
    }

    private fun setupTextWatchers() {
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                loginViewModel.setEmail(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })
    }

    private fun setupKeyboardListener() {
        val rootView = window.decorView.rootView
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.height
            val keypadHeight = screenHeight - rect.bottom
            if (keypadHeight > screenHeight * 0.3) {
                binding.root.scrollTo(0, keypadHeight)
            }
        }
    }


    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val uid = auth.currentUser?.uid
                            if (uid != null) {
                                firestore.collection("users").document(uid).get()
                                    .addOnSuccessListener { document ->
                                        if (document.exists()) {
                                            val userType = document.getString("userType") ?: "kupac"
                                            navigateToHome(userType)
                                        } else {
                                            Toast.makeText(this, "Podaci o korisniku nisu pronađeni", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Greška pri dohvatu podataka: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                Toast.makeText(this, "Greška: Korisnik nije autentificiran", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, "Prijava neuspješna: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
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

    private fun navigateToHome(userType: String) {
        val intent = if (userType == "majstor") {
            Intent(this, HomeCraftsmanActivity::class.java)
        } else {
            Intent(this, HomeCustomerActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}
