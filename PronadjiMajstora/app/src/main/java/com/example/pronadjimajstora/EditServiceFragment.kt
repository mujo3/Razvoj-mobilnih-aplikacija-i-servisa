package com.example.pronadjimajstora

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.pronadjimajstora.databinding.FragmentEditServiceBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class EditServiceFragment : Fragment() {

    private var _binding: FragmentEditServiceBinding? = null
    private val binding get() = _binding!!

    private var imageUri: Uri? = null
    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentService: Service
    private var serviceId: String = ""

    // Aktivnost za odabir slike
    private val pickImage = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            binding.ivServiceImage.setImageURI(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEditServiceBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Preuzimanje proslijeđenog serviceId iz argumenata
        arguments?.let {
            serviceId = it.getString("serviceId") ?: ""
            if (serviceId.isNotEmpty()) {
                loadServiceData()
            } else {
                Toast.makeText(requireContext(), "Nije pronađen ID usluge", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        }

        binding.btnAddImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnSubmit.setOnClickListener {
            if (validateInputs()) {
                if (imageUri != null) {
                    uploadImageAndUpdateService()
                } else {
                    updateService(currentService.imageUrl)
                }
            }
        }
    }

    private fun loadServiceData() {
        firestore.collection("services").document(serviceId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    currentService = document.toObject(Service::class.java)!!
                    populateFields()
                } else {
                    Toast.makeText(requireContext(), "Usluga ne postoji", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Greška pri učitavanju usluge: ${e.message}", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
    }

    private fun populateFields() {
        with(binding) {
            etTitle.setText(currentService.name)
            etDescription.setText(currentService.description)
            etPrice.setText(currentService.price.toString()) // Ažurirano polje

            // Učitavanje postojeće slike, ako postoji
            if (currentService.imageUrl != "default") {
                Glide.with(requireContext())
                    .load(currentService.imageUrl)
                    .into(ivServiceImage)
            } else {
                ivServiceImage.setImageResource(R.drawable.ic_add_photo)
            }
        }
    }

    private fun validateInputs(): Boolean {
        val title = binding.etTitle.text.toString()
        val description = binding.etDescription.text.toString()
        val priceText = binding.etPrice.text.toString()
        return when {
            title.isEmpty() -> {
                binding.etTitle.error = "Unesite naziv usluge"
                false
            }
            description.isEmpty() -> {
                binding.etDescription.error = "Unesite opis usluge"
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

    private fun uploadImageAndUpdateService() {
        val storageRef = FirebaseStorage.getInstance().reference
            .child("service_images/${System.currentTimeMillis()}.jpg")
        imageUri?.let { uri ->
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        updateService(downloadUri.toString())
                    }
                }
                .addOnFailureListener { e ->
                    Snackbar.make(binding.root, "Greška pri uploadu slike: ${e.message}", Snackbar.LENGTH_LONG).show()
                }
        }
    }

    private fun updateService(imageUrl: String) {
        val updatedService = currentService.copy(
            name = binding.etTitle.text.toString(),
            description = binding.etDescription.text.toString(),
            price = binding.etPrice.text.toString().toDouble(), // Ažurirano polje
            imageUrl = imageUrl
        )
        firestore.collection("services").document(updatedService.id)
            .set(updatedService)
            .addOnSuccessListener {
                Snackbar.make(binding.root, "Usluga uspješno ažurirana!", Snackbar.LENGTH_LONG).show()
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener { e ->
                Snackbar.make(binding.root, "Greška: ${e.message}", Snackbar.LENGTH_LONG).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        // Kreiramo novi instance fragmenta i prosljeđujemo serviceId
        fun newInstance(serviceId: String): EditServiceFragment {
            val fragment = EditServiceFragment()
            val args = Bundle()
            args.putString("serviceId", serviceId)
            fragment.arguments = args
            return fragment
        }
    }
}
