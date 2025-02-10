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
                val newService = createService()
                saveServiceToFirestore(newService)
            }
        }
    }

    private fun validateInputs(): Boolean {
        return when {
            binding.etTitle.text.toString().isEmpty() -> {
                binding.etTitle.error = "Unesite naslov"
                false
            }
            binding.etDescription.text.toString().isEmpty() -> {
                binding.etDescription.error = "Unesite opis"
                false
            }
            binding.etPrice.text.toString().isEmpty() -> {
                binding.etPrice.error = "Unesite cijenu"
                false
            }
            else -> true
        }
    }

    private fun createService(): Service {
        return Service(
            id = System.currentTimeMillis().toString(),
            name = binding.etTitle.text.toString(),
            category = "Opće", // U budućnosti dodati izbor kategorije
            craftsman = auth.currentUser?.uid ?: "nepoznat", // Sprema se UID prijavljenog korisnika
            rating = 4.5f,
            location = "Sarajevo", // Možete dohvatiti iz profila korisnika
            priceRange = "${binding.etPrice.text} KM",
            priceRangeMax = binding.etPrice.text.toString().toInt(),
            description = binding.etDescription.text.toString()
        )
    }

    private fun saveServiceToFirestore(service: Service) {
        firestore.collection("services").document(service.id)
            .set(service)
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