package com.example.pronadjimajstora

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pronadjimajstora.databinding.FragmentRegisterBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Konfiguracija Google prijave
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Klik listener za dugme Registracija
        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (validateInputs(email, password, confirmPassword)) {
                registerUser(email, password)
            }
        }

        // Klik listener za dugme Google prijave
        binding.btnGoogle.setOnClickListener {
            signInWithGoogle()
        }

        // **Dodajemo klik listener za dugme Facebook prijave**
        binding.btnFacebookLogin.setOnClickListener {
            Toast.makeText(this, "Funkcionalnost u izradi", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let { token ->
                    firebaseAuthWithGoogle(token)
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Google prijava neuspješna: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    checkIfProfileExists(task.result.user?.uid ?: "")
                } else {
                    Toast.makeText(this, "Google prijava neuspješna: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun validateInputs(
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        return when {
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
            else -> true
        }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    checkIfProfileExists(task.result.user?.uid ?: "")
                } else {
                    Toast.makeText(this, "Registracija neuspješna: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkIfProfileExists(uid: String) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userType = document.getString("userType") ?: "kupac"
                    navigateToHome(userType)
                } else {
                    // Prebaci na profil setup
                    val user = auth.currentUser
                    val intent = Intent(this, ProfileSetupActivity::class.java).apply {
                        putExtra("email", user?.email)
                        putExtra("name", user?.displayName)
                    }
                    startActivity(intent)
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Greška pri provjeri profila: ${e.message}", Toast.LENGTH_SHORT).show()
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
