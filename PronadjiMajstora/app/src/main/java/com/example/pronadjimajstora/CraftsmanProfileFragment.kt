package com.example.pronadjimajstora

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.pronadjimajstora.databinding.FragmentCraftsmanProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CraftsmanProfileFragment : Fragment() {

    private var _binding: FragmentCraftsmanProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    // Koristimo CraftsmanProfileViewModel za čuvanje stanja profila
    private val profileViewModel: CraftsmanProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
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
                        // Ažuriramo ViewModel s učitanim podacima (ako već nisu postavljeni)
                        profileViewModel.apply {
                            if (name.value.isNullOrEmpty()) setName(it.getString("name") ?: "")
                            if (specialization.value.isNullOrEmpty()) setSpecialization(it.getString("specialization") ?: "")
                            if (location.value.isNullOrEmpty()) setLocation(it.getString("location") ?: "")
                            if (phone.value.isNullOrEmpty()) setPhone(it.getString("phone") ?: "")
                            if (email.value.isNullOrEmpty()) setEmail(it.getString("email") ?: "")
                            if (bio.value.isNullOrEmpty()) setBio(it.getString("bio") ?: "")
                            if (profilePicUrl.value.isNullOrEmpty()) setProfilePicUrl(it.getString("profilePicUrl") ?: "")
                            if (rating.value == 0f) setRating(it.getDouble("rating")?.toFloat() ?: 0f)
                        }
                        updateUI()
                    }
                }
        }
    }

    private fun updateUI() {
        with(binding) {
            tvName.text = (profileViewModel.name.value ?: "").ifEmpty { "Unesite ime" }
            tvSpecialization.text = (profileViewModel.specialization.value ?: "").ifEmpty { "Unesite specijalizaciju" }
            tvLocation.text = (profileViewModel.location.value ?: "").ifEmpty { "Lokacija nije postavljena" }
            tvPhone.text = (profileViewModel.phone.value ?: "").ifEmpty { "Telefon nije postavljen" }
            tvEmail.text = (profileViewModel.email.value ?: "").ifEmpty { "Email nije postavljen" }
            tvBio.text = (profileViewModel.bio.value ?: "").ifEmpty { "Opišite se" }
            // Za rating, primjer – prikaz zvjezdica
            val ratingInt = profileViewModel.rating.value?.toInt() ?: 0
            tvRating.text = "★".repeat(ratingInt)
            val profilePicUrl = profileViewModel.profilePicUrl.value
            if (!profilePicUrl.isNullOrEmpty()) {
                Glide.with(requireContext()).load(profilePicUrl).into(binding.ivProfilePicture)
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
        val etName = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etName)
        val etSpecialization = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etSpecialization)
        val etLocation = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etLocation)
        val etPhone = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPhone)
        val etEmail = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEmail)
        val etBio = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etBio)
        val ivDialogProfilePicture = dialogView.findViewById<android.widget.ImageView>(R.id.ivDialogProfilePicture)

        // Postavi trenutno spremljene podatke iz ViewModel‑a
        etName.setText(profileViewModel.name.value)
        etSpecialization.setText(profileViewModel.specialization.value)
        etLocation.setText(profileViewModel.location.value)
        etPhone.setText(profileViewModel.phone.value)
        etEmail.setText(profileViewModel.email.value)
        etBio.setText(profileViewModel.bio.value)

        val profilePicUrl = profileViewModel.profilePicUrl.value
        if (!profilePicUrl.isNullOrEmpty()) {
            Glide.with(requireContext()).load(profilePicUrl).into(ivDialogProfilePicture)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Uredi profil")
            .setView(dialogView)
            .setPositiveButton("Spremi") { dialog, _ ->
                // Spremi izmjene u Firestore i ažuriraj ViewModel
                val updates = mapOf(
                    "name" to etName.text.toString(),
                    "specialization" to etSpecialization.text.toString(),
                    "location" to etLocation.text.toString(),
                    "phone" to etPhone.text.toString(),
                    "email" to etEmail.text.toString(),
                    "bio" to etBio.text.toString()
                )
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    firestore.collection("users").document(currentUser.uid)
                        .update(updates)
                        .addOnSuccessListener {
                            profileViewModel.setName(etName.text.toString())
                            profileViewModel.setSpecialization(etSpecialization.text.toString())
                            profileViewModel.setLocation(etLocation.text.toString())
                            profileViewModel.setPhone(etPhone.text.toString())
                            profileViewModel.setEmail(etEmail.text.toString())
                            profileViewModel.setBio(etBio.text.toString())
                            updateUI()
                            Toast.makeText(requireContext(), "Profil ažuriran", Toast.LENGTH_SHORT).show()
                        }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Odustani") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = CraftsmanProfileFragment()
    }
}
