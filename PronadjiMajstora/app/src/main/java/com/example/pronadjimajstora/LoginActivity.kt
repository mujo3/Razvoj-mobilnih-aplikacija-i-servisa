package com.example.pronadjimajstora

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        val logoImageView = findViewById<ImageView>(R.id.logoImageView)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<Button>(R.id.registerButton)


        logoImageView.setOnClickListener {

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        loginButton.setOnClickListener {
            Toast.makeText(this, "Funkcionalnost u izradi", Toast.LENGTH_SHORT).show()
        }

        registerButton.setOnClickListener {
            Toast.makeText(this, "Funkcionalnost u izradi", Toast.LENGTH_SHORT).show()
        }

    }
}