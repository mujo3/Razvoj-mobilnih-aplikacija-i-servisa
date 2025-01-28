package com.example.pronadjimajstora

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pronadjimajstora.databinding.FragmentRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: FragmentRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()
            val userType = getSelectedUserType()

            if (validateInputs(email, password, confirmPassword, userType)) {
                // Direktno preusmjeri na Home ekran
                navigateToHome()
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

    private fun validateInputs(email: String, password: String, confirmPassword: String, userType: String): Boolean {
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
            userType.isEmpty() -> {
                Toast.makeText(this, "Odaberite tip korisnika", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun navigateToHome() {
        val intent = if (getSelectedUserType() == "majstor") {
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