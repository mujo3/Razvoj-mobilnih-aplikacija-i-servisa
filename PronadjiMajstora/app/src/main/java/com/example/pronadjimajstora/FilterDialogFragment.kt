package com.example.pronadjimajstora

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import android.widget.SeekBar
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore

class FilterDialogFragment : DialogFragment() {

    interface FilterListener {
        fun onFiltersApplied(category: String, location: String, maxPrice: Double)
    }

    private var listener: FilterListener? = null

    private lateinit var spinnerCategory: MaterialAutoCompleteTextView
    private lateinit var etLocation: TextInputEditText
    private lateinit var seekBarPrice: SeekBar
    private lateinit var tvMaxPrice: TextView
    private lateinit var btnApplyFilters: MaterialButton

    fun setFilterListener(listener: FilterListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicijalizacija UI komponenti
        spinnerCategory = view.findViewById(R.id.spinnerCategory)
        etLocation = view.findViewById(R.id.etLocation)
        seekBarPrice = view.findViewById(R.id.seekBarPrice)
        tvMaxPrice = view.findViewById(R.id.tvMaxPrice)
        btnApplyFilters = view.findViewById(R.id.btnApplyFilters)

        fetchSpecializations()
        setupSeekBar()

        btnApplyFilters.setOnClickListener {
            applyFilters()
        }
    }

    private fun fetchSpecializations() {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("services")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val specializationsSet = mutableSetOf<String>()
                for (document in querySnapshot.documents) {
                    val specialization = document.getString("specialization")
                    if (!specialization.isNullOrEmpty()) {
                        specializationsSet.add(specialization)
                    }
                }
                val categories = specializationsSet.toList().sorted()
                setupCategorySpinner(categories)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Greška pri dohvaćanju kategorija: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun setupCategorySpinner(categories: List<String>) {
        val allCategories = mutableListOf("Sve kategorije")
        allCategories.addAll(categories)

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            allCategories
        )

        spinnerCategory.setAdapter(adapter)
        spinnerCategory.setOnItemClickListener { _, _, position, _ ->
            spinnerCategory.setText(allCategories[position], false)
            spinnerCategory.contentDescription = "Odabrana kategorija: ${allCategories[position]}"
        }
    }

    private fun setupSeekBar() {
        seekBarPrice.max = 5000
        seekBarPrice.progress = 2500
        tvMaxPrice.text = "Maksimalna cijena: ${seekBarPrice.progress} KM"

        seekBarPrice.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvMaxPrice.text = "Maksimalna cijena: $progress KM"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun applyFilters() {
        val selectedCategory = spinnerCategory.text.toString()
        val location = etLocation.text.toString()
        val maxPrice = seekBarPrice.progress.toDouble()

        listener?.onFiltersApplied(
            if (selectedCategory.isEmpty()) "Sve kategorije" else selectedCategory,
            location,
            if (maxPrice == 0.0) 5000.0 else maxPrice
        )
        dismiss()
    }
}
