package com.example.pronadjimajstora

import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.pronadjimajstora.databinding.FragmentEditServiceBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class EditServiceFragment : Fragment() {
    private var _binding: FragmentEditServiceBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentService: Service
    private var imageUri: Uri? = null
    private var serviceId: String = ""

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
        serviceId = arguments?.getString("serviceId") ?: ""
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupKeyboardListener()
        loadServiceData()
        setupUI()
    }

    private fun setupKeyboardListener() {
        val rootView = binding.root
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.height
            val keypadHeight = screenHeight - rect.bottom
            if (keypadHeight > screenHeight * 0.15) {
                rootView.scrollTo(0, keypadHeight)
            }
        }
    }

    private fun loadServiceData() {
        if (serviceId.isEmpty()) return

        firestore.collection("services").document(serviceId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    currentService = document.toObject(Service::class.java)!!
                    populateFields()
                } else {
                    showError("Usluga ne postoji")
                }
            }
            .addOnFailureListener { e ->
                showError("Greška pri učitavanju: ${e.message}")
            }
    }

    private fun populateFields() {
        with(binding) {
            etTitle.setText(currentService.name)
            etDescription.setText(currentService.description)
            etPrice.setText(currentService.price.toString())

            if (currentService.imageUrl != "default") {
                Glide.with(requireContext())
                    .load(currentService.imageUrl)
                    .into(ivServiceImage)
            }
        }
    }

    private fun setupUI() {
        binding.btnAddImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnSubmit.setOnClickListener {
            if (validateInputs()) {
                imageUri?.let { uri ->
                    uploadImageAndUpdate(uri)
                } ?: updateService(currentService.imageUrl)
            }
        }
    }

    private fun validateInputs(): Boolean {
        return validateField(binding.etTitle.text.toString(), "naslov") &&
                validateField(binding.etDescription.text.toString(), "opis") &&
                validatePrice(binding.etPrice.text.toString())
    }

    private fun validateField(value: String, fieldName: String): Boolean {
        if (value.isEmpty()) {
            showError("Unesite $fieldName")
            return false
        }
        return true
    }

    private fun validatePrice(price: String): Boolean {
        return try {
            price.toDouble()
            true
        } catch (e: NumberFormatException) {
            showError("Nevažeći format cijene")
            false
        }
    }

    private fun uploadImageAndUpdate(uri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
            .child("service_images/${System.currentTimeMillis()}.jpg")

        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { url ->
                    updateService(url.toString())
                }
            }
            .addOnFailureListener { e ->
                showError("Greška pri uploadu: ${e.message}")
            }
    }

    private fun updateService(imageUrl: String) {
        val updatedService = currentService.copy(
            name = binding.etTitle.text.toString(),
            description = binding.etDescription.text.toString(),
            price = binding.etPrice.text.toString().toDouble(),
            imageUrl = imageUrl
        )

        firestore.collection("services").document(serviceId)
            .set(updatedService)
            .addOnSuccessListener {
                showConfirmation()
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener { e ->
                showError("Greška pri ažuriranju: ${e.message}")
            }
    }

    private fun showConfirmation() {
        Snackbar.make(binding.root, "Usluga ažurirana!", Snackbar.LENGTH_LONG).show()
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(serviceId: String): EditServiceFragment {
            return EditServiceFragment().apply {
                arguments = Bundle().apply {
                    putString("serviceId", serviceId)
                }
            }
        }
    }
}