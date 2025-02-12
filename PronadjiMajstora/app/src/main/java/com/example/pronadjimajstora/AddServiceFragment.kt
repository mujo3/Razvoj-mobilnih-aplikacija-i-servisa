package com.example.pronadjimajstora

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.pronadjimajstora.databinding.FragmentAddServiceBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class AddServiceFragment : Fragment() {
    private var _binding: FragmentAddServiceBinding? = null
    private val binding get() = _binding!!
    private var imageUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            binding.ivServiceImage.setImageURI(it)
        }
    }

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddServiceBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.btnAddImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnSubmit.setOnClickListener {
            if (validateInputs()) {
                val currentUserUid = auth.currentUser?.uid
                if (currentUserUid == null) {
                    Snackbar.make(binding.root, "Korisnik nije prijavljen", Snackbar.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                firestore.collection("users").document(currentUserUid)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val craftsmanName = document.getString("name") ?: "Nepoznato"
                            val craftsmanLocation = document.getString("location") ?: "Nepoznato"
                            val craftsmanRating = document.getDouble("rating")?.toFloat() ?: 0f
                            val craftsmanSpecialization = document.getString("specialization") ?: "Nepoznato"

                            if (imageUri != null) {
                                uploadImageAndCreateService(
                                    craftsmanName,
                                    craftsmanLocation,
                                    craftsmanRating,
                                    craftsmanSpecialization
                                )
                            } else {
                                val defaultImageMarker = "default"
                                val newService = createService(
                                    craftsmanName,
                                    craftsmanLocation,
                                    craftsmanRating,
                                    craftsmanSpecialization,
                                    defaultImageMarker
                                )
                                saveServiceToFirestore(newService)
                            }
                        } else {
                            Snackbar.make(binding.root, "Korisnički podaci nisu dostupni", Snackbar.LENGTH_LONG).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Snackbar.make(binding.root, "Greška pri dohvaćanju korisničkih podataka: ${e.message}", Snackbar.LENGTH_LONG).show()
                    }
            }
        }
    }

    private fun validateInputs(): Boolean {
        val title = binding.etTitle.text.toString()
        val description = binding.etDescription.text.toString()
        val priceText = binding.etPrice.text.toString()

        return when {
            title.isEmpty() -> {
                binding.etTitle.error = "Unesite naslov"
                false
            }
            description.isEmpty() -> {
                binding.etDescription.error = "Unesite opis"
                false
            }
            priceText.isEmpty() -> {
                binding.etPrice.error = "Unesite cijenu"
                false
            }
            else -> {
                try {
                    priceText.toDouble()
                    true
                } catch (e: NumberFormatException) {
                    binding.etPrice.error = "Cijena mora biti validan broj"
                    false
                }
            }
        }
    }

    private fun createService(
        craftsmanName: String,
        craftsmanLocation: String,
        craftsmanRating: Float,
        craftsmanSpecialization: String,
        imageUrl: String
    ): Service {
        return Service(
            name = binding.etTitle.text.toString(),
            specialization = craftsmanSpecialization, // Koristimo polje 'specialization' kao kategoriju
            craftsman = craftsmanName,
            rating = craftsmanRating,
            location = craftsmanLocation,
            price = binding.etPrice.text.toString().toDouble(),
            description = binding.etDescription.text.toString(),
            imageUrl = imageUrl
        )
    }

    private fun uploadImageAndCreateService(
        craftsmanName: String,
        craftsmanLocation: String,
        craftsmanRating: Float,
        craftsmanSpecialization: String
    ) {
        val storageRef = FirebaseStorage.getInstance().reference.child("service_images/${System.currentTimeMillis()}.jpg")
        imageUri?.let { uri ->
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        val imageUrl = downloadUri.toString()
                        val newService = createService(
                            craftsmanName,
                            craftsmanLocation,
                            craftsmanRating,
                            craftsmanSpecialization,
                            imageUrl
                        )
                        saveServiceToFirestore(newService)
                    }
                }
                .addOnFailureListener { e ->
                    Snackbar.make(binding.root, "Greška pri uploadu slike: ${e.message}", Snackbar.LENGTH_LONG).show()
                }
        }
    }

    private fun saveServiceToFirestore(service: Service) {
        val documentRef = firestore.collection("services").document()
        val serviceWithId = service.copy(id = documentRef.id)
        documentRef.set(serviceWithId)
            .addOnSuccessListener {
                showConfirmation()
                clearForm()
            }
            .addOnFailureListener { e ->
                Snackbar.make(binding.root, "Greška: ${e.message}", Snackbar.LENGTH_LONG).show()
            }
    }

    private fun showConfirmation() {
        Snackbar.make(binding.root, "Usluga uspješno dodana!", Snackbar.LENGTH_LONG)
            .setAction("OK") {}
            .show()
    }

    private fun clearForm() {
        with(binding) {
            etTitle.text?.clear()
            etDescription.text?.clear()
            etPrice.text?.clear()
            ivServiceImage.setImageResource(R.drawable.ic_add_photo)
            imageUri = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = AddServiceFragment()
    }
}
