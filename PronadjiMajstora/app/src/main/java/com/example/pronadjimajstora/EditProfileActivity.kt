package com.example.pronadjimajstora

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.pronadjimajstora.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private val getImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { uploadImage(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupUI()
        populateFields()
    }

    private fun setupUI() {
        binding.ivProfilePicture.setOnClickListener {
            getImageLauncher.launch("image/*")
        }
        binding.btnSave.setOnClickListener {
            saveProfileChanges()
            finish()
        }
    }

    private fun populateFields() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    document?.let {
                        binding.etName.setText(it.getString("name"))
                        binding.etEmail.setText(it.getString("email"))
                        binding.etLocation.setText(it.getString("location"))

                        val profilePicUrl = it.getString("profilePicUrl")
                        if (!profilePicUrl.isNullOrEmpty()) {
                            Glide.with(this).load(profilePicUrl).into(binding.ivProfilePicture)
                        }
                    }
                }
        }
    }

    private fun saveProfileChanges() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val updates = hashMapOf<String, Any>(
                "name" to binding.etName.text.toString(),
                "email" to binding.etEmail.text.toString(),
                "location" to binding.etLocation.text.toString()
            )

            firestore.collection("users").document(currentUser.uid)
                .update(updates)
                .addOnSuccessListener {
                    Toast.makeText(this, "Promjene spremljene", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Greška: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun uploadImage(uri: Uri) {
        val userId = auth.currentUser?.uid ?: return
        val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/$userId.jpg")
        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val downloadUrl = downloadUri.toString()
                    firestore.collection("users").document(userId)
                        .update("profilePicUrl", downloadUrl)
                        .addOnSuccessListener {
                            Glide.with(this).load(downloadUrl).into(binding.ivProfilePicture)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}