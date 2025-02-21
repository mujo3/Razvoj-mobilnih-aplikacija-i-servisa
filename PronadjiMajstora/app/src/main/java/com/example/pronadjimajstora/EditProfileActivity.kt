package com.example.pronadjimajstora

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
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


    private val profileViewModel: ProfileViewModel by viewModels()

    private val getImageLauncher =
        registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetContent()) { uri: Uri? ->
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
        setupTextWatchers()
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

                        if (profileViewModel.name.value.isNullOrEmpty()) {
                            profileViewModel.setName(it.getString("name") ?: "")
                        }
                        if (profileViewModel.email.value.isNullOrEmpty()) {
                            profileViewModel.setEmail(it.getString("email") ?: "")
                        }
                        if (profileViewModel.location.value.isNullOrEmpty()) {
                            profileViewModel.setLocation(it.getString("location") ?: "")
                        }
                        if (profileViewModel.profilePicUrl.value.isNullOrEmpty()) {
                            profileViewModel.setProfilePicUrl(it.getString("profilePicUrl") ?: "")
                        }

                        binding.etName.setText(profileViewModel.name.value)
                        binding.etEmail.setText(profileViewModel.email.value)
                        binding.etLocation.setText(profileViewModel.location.value)

                        val profilePicUrl = profileViewModel.profilePicUrl.value
                        if (!profilePicUrl.isNullOrEmpty()) {
                            Glide.with(this).load(profilePicUrl).into(binding.ivProfilePicture)
                        }
                    }
                }
        }
    }

    private fun setupTextWatchers() {
        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                profileViewModel.setName(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                profileViewModel.setEmail(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.etLocation.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                profileViewModel.setLocation(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun saveProfileChanges() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val updates = hashMapOf<String, Any>(
                "name" to profileViewModel.name.value.orEmpty(),
                "email" to profileViewModel.email.value.orEmpty(),
                "location" to profileViewModel.location.value.orEmpty()
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
                    profileViewModel.setProfilePicUrl(downloadUrl)
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
