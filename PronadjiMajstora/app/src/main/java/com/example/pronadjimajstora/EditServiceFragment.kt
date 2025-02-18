package com.example.pronadjimajstora

import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.pronadjimajstora.databinding.FragmentEditServiceBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.FirebaseStorage

class EditServiceFragment : Fragment() {
    private var _binding: FragmentEditServiceBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentCraftsmanData: CraftsmanData
    private var imageUri: Uri? = null
    private var serviceId: String = ""

    // Koristimo EditServiceViewModel za čuvanje unosa
    private val viewModel: EditServiceViewModel by viewModels()

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
        setupTextWatchers()
        // Pokrećemo image picker kada korisnik klikne na sliku
        binding.ivServiceImage.setOnClickListener {
            pickImage.launch("image/*")
        }
        loadServiceData()
        binding.btnSubmit.setOnClickListener {
            if (validateInputs()) {
                if (::currentCraftsmanData.isInitialized) {
                    imageUri?.let { uri ->
                        uploadImageAndUpdate(uri)
                    } ?: updateService(viewModel.imageUrl.value ?: currentCraftsmanData.imageUrl)
                } else {
                    showError("Podaci nisu učitani")
                }
            }
        }
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

    private fun setupTextWatchers() {
        binding.etTitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setTitle(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })
        binding.etDescription.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setDescription(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })
        binding.etPrice.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setPrice(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })
    }

    private fun loadServiceData() {
        if (serviceId.isEmpty()) return

        firestore.collection("services").document(serviceId).get()
            .addOnSuccessListener { document ->
                if (!isAdded || _binding == null) return@addOnSuccessListener

                if (document.exists()) {
                    // Učitavamo podatke iz dokumenta i postavljamo id iz document.id
                    currentCraftsmanData = document.toObject(CraftsmanData::class.java)!!
                    currentCraftsmanData.id = document.id
                    // Ažuriramo ViewModel s dohvaćenim podacima
                    viewModel.setTitle(currentCraftsmanData.name)
                    viewModel.setDescription(currentCraftsmanData.description)
                    viewModel.setPrice(currentCraftsmanData.price.toString())
                    viewModel.setImageUrl(currentCraftsmanData.imageUrl)
                    Log.d("EditServiceFragment", "Podaci učitani: $currentCraftsmanData")
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
        _binding?.let { bindingSafe ->
            with(bindingSafe) {
                etTitle.setText(viewModel.title.value)
                etDescription.setText(viewModel.description.value)
                etPrice.setText(viewModel.price.value)
                if (viewModel.imageUrl.value != "default") {
                    Glide.with(requireContext())
                        .load(viewModel.imageUrl.value)
                        .into(ivServiceImage)
                }
                Log.d("EditServiceFragment", "Polja popunjena")
            }
        }
    }

    private fun validateInputs(): Boolean {
        return validateField(binding.etTitle.text.toString(), binding.etTitle, "naslov") &&
                validateField(binding.etDescription.text.toString(), binding.etDescription, "opis") &&
                validatePrice(binding.etPrice.text.toString())
    }

    private fun validateField(value: String, view: View, fieldName: String): Boolean {
        if (value.isEmpty()) {
            if (view is TextInputEditText) view.error = "Unesite $fieldName"
            return false
        }
        return true
    }

    private fun validatePrice(price: String): Boolean {
        return try {
            price.toDouble()
            true
        } catch (e: NumberFormatException) {
            binding.etPrice.error = "Nevažeći format cijene"
            false
        }
    }

    private fun uploadImageAndUpdate(uri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
            .child("service_images/${System.currentTimeMillis()}.jpg")

        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { url ->
                    viewModel.setImageUrl(url.toString())
                    updateService(url.toString())
                }
            }
            .addOnFailureListener { e ->
                showError("Greška pri uploadu: ${e.message}")
            }
    }

    private fun updateService(imageUrl: String) {
        if (!::currentCraftsmanData.isInitialized) {
            showError("Podaci nisu učitani")
            return
        }

        val updatedCraftsmanData = currentCraftsmanData.copy(
            name = binding.etTitle.text.toString(), // naziv usluge
            description = binding.etDescription.text.toString(),
            price = binding.etPrice.text.toString().toDouble(),
            imageUrl = imageUrl
        )

        firestore.collection("services").document(serviceId)
            .set(updatedCraftsmanData)
            .addOnSuccessListener {
                Log.d("EditServiceFragment", "Podaci ažurirani: $updatedCraftsmanData")
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
        _binding?.let {
            Snackbar.make(it.root, message, Snackbar.LENGTH_LONG).show()
        }
        Log.e("EditServiceFragment", message)
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
