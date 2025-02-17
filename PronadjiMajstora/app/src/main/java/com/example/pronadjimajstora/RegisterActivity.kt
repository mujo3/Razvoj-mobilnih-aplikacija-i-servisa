package com.example.pronadjimajstora

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.pronadjimajstora.databinding.FragmentRegisterBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient

    // Inicijaliziramo RegisterViewModel – stanje emaila će se čuvati
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupKeyboardListener()
        initializeAuth()
        setupClickListeners()
        setupTextWatchers()
        // Obnova spremljene vrijednosti emaila iz ViewModel-a
        binding.etEmail.setText(registerViewModel.email.value)
    }

    private fun setupTextWatchers() {
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                registerViewModel.setEmail(s.toString())
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
            if (keypadHeight > screenHeight * 0.15) {
                binding.root.scrollTo(0, keypadHeight)
            }
        }
    }

    private fun initializeAuth() {
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener { attemptRegistration() }
        binding.btnGoogle.setOnClickListener { signInWithGoogle() }
        binding.btnFacebookLogin.setOnClickListener { showFacebookMessage() }
    }

    private fun attemptRegistration() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        when {
            !isEmailValid(email) -> showError("Nevalidan email")
            !isPasswordValid(password) -> showError("Šifra mora imati najmanje 6 znakova")
            password != confirmPassword -> showError("Šifre se ne podudaraju")
            else -> registerUser(email, password)
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    checkUserProfile()
                } else {
                    showError("Registracija neuspješna: ${task.exception?.message}")
                }
            }
    }

    private fun checkUserProfile() {
        auth.currentUser?.uid?.let { uid ->
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        navigateToHome(document.getString("userType") ?: "kupac")
                    } else {
                        navigateToProfileSetup()
                    }
                }
        } ?: showError("Korisnički ID nije dostupan")
    }

    private fun navigateToHome(userType: String) {
        val intent = when (userType) {
            "majstor" -> Intent(this, HomeCraftsmanActivity::class.java)
            else -> Intent(this, HomeCustomerActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

    private fun navigateToProfileSetup() {
        val user = auth.currentUser
        Intent(this, ProfileSetupActivity::class.java).apply {
            putExtra("email", user?.email)
            putExtra("name", user?.displayName)
        }.also { startActivity(it) }
        finish()
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            handleGoogleSignIn(data)
        }
    }

    private fun handleGoogleSignIn(data: Intent?) {
        try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java)
            account.idToken?.let { firebaseAuthWithGoogle(it) }
        } catch (e: ApiException) {
            showError("Google prijava neuspješna: ${e.message}")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    checkUserProfile()
                } else {
                    showError("Prijava neuspješna: ${task.exception?.message}")
                }
            }
    }

    private fun showFacebookMessage() {
        showError("Facebook prijava trenutno nije dostupna")
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
        when {
            message.contains("email", true) -> binding.etEmail.error = message
            message.contains("šifr", true) -> binding.etPassword.error = message
            else -> binding.etConfirmPassword.error = message
        }
    }

    companion object {
        const val RC_GOOGLE_SIGN_IN = 1001
    }
}
