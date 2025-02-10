package com.example.pronadjimajstora

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.pronadjimajstora.databinding.FragmentCraftsmanProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class CraftsmanProfileFragment : Fragment() {

    private var _binding: FragmentCraftsmanProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCraftsmanProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        setupProfile()
        setupEditButton()
    }

    private fun setupProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    document?.let {
                        with(binding) {
                            tvName.text = it.getString("name") ?: "Unesite ime"
                            tvSpecialization.text = it.getString("specialization") ?: "Unesite specijalizaciju"
                            tvRating.text = "★".repeat(it.getDouble("rating")?.toInt() ?: 0)
                            tvLocation.text = it.getString("location") ?: "Lokacija nije postavljena"
                            tvPhone.text = it.getString("phone") ?: "Telefon nije postavljen"
                            tvEmail.text = it.getString("email") ?: "Email nije postavljen"
                            tvBio.text = it.getString("bio") ?: "Opišite se"

                            val profilePicUrl = it.getString("profilePicUrl")
                            if (!profilePicUrl.isNullOrEmpty()) {
                                Glide.with(requireContext()).load(profilePicUrl).into(binding.ivProfilePicture)
                            }
                        }
                    }
                }
        }
    }

    private fun setupEditButton() {
        binding.fabEditProfile.setOnClickListener {
            showEditProfileDialog()
        }
    }

    private fun showEditProfileDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val currentUser = auth.currentUser ?: return

        firestore.collection("users").document(currentUser.uid).get()
            .addOnSuccessListener { document ->
                val etName = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etName)
                val etSpecialization = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etSpecialization)
                val etLocation = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etLocation)
                val etPhone = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPhone)
                val etEmail = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEmail)
                val etBio = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etBio)
                val ivDialogProfilePicture = dialogView.findViewById<ImageView>(R.id.ivDialogProfilePicture)

                etName.setText(document.getString("name"))
                etSpecialization.setText(document.getString("specialization"))
                etLocation.setText(document.getString("location"))
                etPhone.setText(document.getString("phone"))
                etEmail.setText(document.getString("email"))
                etBio.setText(document.getString("bio"))

                val profilePicUrl = document.getString("profilePicUrl")
                if (!profilePicUrl.isNullOrEmpty()) {
                    Glide.with(requireContext()).load(profilePicUrl).into(ivDialogProfilePicture)
                }

                ivDialogProfilePicture.setOnClickListener {
                    getImageLauncher.launch("image/*")
                }

                val builder = MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Uredi profil")
                    .setView(dialogView)
                    .setPositiveButton("Spremi") { dialog, _ ->
                        val updates = hashMapOf<String, Any>(
                            "name" to etName.text.toString(),
                            "specialization" to etSpecialization.text.toString(),
                            "location" to etLocation.text.toString(),
                            "phone" to etPhone.text.toString(),
                            "email" to etEmail.text.toString(),
                            "bio" to etBio.text.toString()
                        )

                        firestore.collection("users").document(currentUser.uid)
                            .update(updates)
                            .addOnSuccessListener {
                                setupProfile()
                                Toast.makeText(requireContext(), "Profil ažuriran", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(requireContext(), "Greška: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        dialog.dismiss()
                    }
                    .setNegativeButton("Odustani") { dialog, _ -> dialog.dismiss() }

                builder.create().show()
            }
    }

    private val getImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { uploadImage(it) }
    }

    private fun uploadImage(uri: Uri) {
        val userId = auth.currentUser?.uid ?: return
        val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/$userId.jpg")
        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    firestore.collection("users").document(userId)
                        .update("profilePicUrl", downloadUri.toString())
                        .addOnSuccessListener {
                            setupProfile()
                        }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = CraftsmanProfileFragment()
    }
}