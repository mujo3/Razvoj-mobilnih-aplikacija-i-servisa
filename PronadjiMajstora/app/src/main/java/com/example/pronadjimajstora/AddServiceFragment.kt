package com.example.pronadjimajstora

import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.pronadjimajstora.databinding.FragmentAddServiceBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.example.pronadjimajstora.CraftsmanData  // Uvoz nove klase

class AddServiceFragment : Fragment() {
    private var _binding: FragmentAddServiceBinding? = null
    private val binding get() = _binding!!
    private var imageUri: Uri? = null
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // Koristimo AddServiceViewModel za čuvanje unosa (npr. naslov, opis, cijena, URL slike)
    private val viewModel: AddServiceViewModel by viewModels()

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            binding.ivServiceImage.setImageURI(it)
        }
    }

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
        setupKeyboardListener()
        setupTextWatchers()
        restoreFields()
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
                    showError("Korisnik nije prijavljen")
                    return@setOnClickListener
                }
                processServiceCreation(currentUserUid)
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

    private fun restoreFields() {
        binding.etTitle.setText(viewModel.title.value)
        binding.etDescription.setText(viewModel.description.value)
        binding.etPrice.setText(viewModel.price.value)
        if (viewModel.imageUrl.value != "default") {

        }
    }

    private fun validateInputs(): Boolean {
        return validateField(binding.etTitle.text.toString(), binding.etTitle, "Naslov") &&
                validateField(binding.etDescription.text.toString(), binding.etDescription, "Opis") &&
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

    private fun processServiceCreation(uid: String) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val serviceData = getServiceData(document)
                    imageUri?.let { uri ->
                        uploadImageAndCreateService(serviceData, uri)
                    } ?: createServiceWithDefaultImage(serviceData)
                } else {
                    showError("Korisnički podaci nisu dostupni")
                }
            }
            .addOnFailureListener { e ->
                showError("Greška pri dohvaćanju podataka: ${e.message}")
            }
    }

    private fun getServiceData(document: DocumentSnapshot): CraftsmanData {
        // Sada kreiramo objekt koji sadrži i podatke iz forme i podatke iz korisničkog dokumenta.
        return CraftsmanData(
            name = viewModel.title.value ?: "",  // naziv usluge
            description = viewModel.description.value ?: "",
            price = viewModel.price.value?.toDoubleOrNull() ?: 0.0,
            craftsman = document.getString("name") ?: "Nepoznato",  // ime majstora
            location = document.getString("location") ?: "Nepoznato",
            rating = document.getDouble("rating")?.toFloat() ?: 0f,
            specialization = document.getString("specialization") ?: "Nepoznato",
            imageUrl = viewModel.imageUrl.value ?: "default",
            category = ""  // ako imate kategoriju, dodajte je ovdje
        )
    }

    private fun uploadImageAndCreateService(data: CraftsmanData, uri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
            .child("service_images/${System.currentTimeMillis()}.jpg")

        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    viewModel.setImageUrl(downloadUri.toString())
                    saveService(data.copy(imageUrl = downloadUri.toString()))
                }
            }
            .addOnFailureListener { e ->
                showError("Greška pri uploadu slike: ${e.message}")
            }
    }

    private fun createServiceWithDefaultImage(data: CraftsmanData) {
        saveService(data.copy(imageUrl = "default"))
    }

    private fun saveService(data: CraftsmanData) {
        firestore.collection("services").add(data.toMap())
            .addOnSuccessListener { documentReference ->
                showConfirmation()
                clearForm()
                // Log ili Toast za potvrdu
                println("Uspješno dodan oglas s ID-em: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                showError("Greška pri spremanju: ${e.message}")
                e.printStackTrace()
            }
    }

    private fun showConfirmation() {
        Snackbar.make(binding.root, "Usluga uspješno dodana!", Snackbar.LENGTH_LONG).show()
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

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = AddServiceFragment()
    }
}
