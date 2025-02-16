package com.example.pronadjimajstora

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
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

    // Varijable za privremeno čuvanje filtera
    private var savedCategory: String? = null
    private var savedLocation: String? = null
    private var savedMaxPrice: Int = 5000

    fun setFilterListener(listener: FilterListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_filter, container, false).apply {
            viewTreeObserver.addOnGlobalLayoutListener {
                val rect = Rect()
                getWindowVisibleDisplayFrame(rect)
                val screenHeight = rootView.height
                val keypadHeight = screenHeight - rect.bottom
                if (keypadHeight > screenHeight * 0.15) {
                    scrollTo(0, keypadHeight)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setupApplyButton()

        // Ako postoji spremljeno stanje, dohvatimo filtere
        if (savedInstanceState != null) {
            savedCategory = savedInstanceState.getString("FILTER_CATEGORY")
            savedLocation = savedInstanceState.getString("FILTER_LOCATION")
            savedMaxPrice = savedInstanceState.getInt("FILTER_MAX_PRICE", 5000)
        }

        setupCategorySpinner()
        setupSeekBar()

        // Vraćamo unesenu lokaciju (ako postoji)
        savedLocation?.let {
            etLocation.setText(it)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        // Dodatno vraćanje stanja – osigurava da su podaci postavljeni nakon što je view u potpunosti kreiran
        savedInstanceState?.let {
            savedCategory = it.getString("FILTER_CATEGORY")
            savedLocation = it.getString("FILTER_LOCATION")
            savedMaxPrice = it.getInt("FILTER_MAX_PRICE", 5000)
            etLocation.setText(savedLocation)
            seekBarPrice.progress = savedMaxPrice
            updatePriceText(savedMaxPrice)
            // Spinner ćemo postaviti u adapter callbacku
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Spremamo trenutno stanje filtera
        outState.putString("FILTER_CATEGORY", spinnerCategory.text.toString())
        outState.putString("FILTER_LOCATION", etLocation.text.toString())
        outState.putInt("FILTER_MAX_PRICE", seekBarPrice.progress)
    }

    private fun initializeViews(view: View) {
        spinnerCategory = view.findViewById(R.id.spinnerCategory)
        etLocation = view.findViewById(R.id.etLocation)
        seekBarPrice = view.findViewById(R.id.seekBarPrice)
        tvMaxPrice = view.findViewById(R.id.tvMaxPrice)
    }

    private fun setupCategorySpinner() {
        FirebaseFirestore.getInstance().collection("services")
            .get()
            .addOnSuccessListener { result ->
                // Izvlačenje specijalizacija iz Firestore i sortiranje po abecedi
                val specializations = result.documents.mapNotNull { doc ->
                    doc.getString("specialization")
                }.distinct().sorted()

                // Kreiramo listu gdje je "Sve kategorije" uvijek prvi element
                val categories = mutableListOf("Sve kategorije")
                categories.addAll(specializations)

                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    categories
                )
                spinnerCategory.setAdapter(adapter)
                // Ako postoji spremljena kategorija, postavljamo ju
                savedCategory?.let { category ->
                    spinnerCategory.setText(category, false)
                }
            }
    }

    private fun setupSeekBar() {
        seekBarPrice.max = 5000
        val initialProgress = if (savedMaxPrice != 0) savedMaxPrice else 5000
        seekBarPrice.progress = initialProgress
        updatePriceText(initialProgress)

        seekBarPrice.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updatePriceText(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeekBar?) { }
        })
    }

    private fun updatePriceText(progress: Int) {
        tvMaxPrice.text = "Maksimalna cijena: $progress KM"
    }

    private fun setupApplyButton() {
        view?.findViewById<MaterialButton>(R.id.btnApplyFilters)?.setOnClickListener {
            applyFilters()
        }
    }

    private fun applyFilters() {
        val category = spinnerCategory.text.toString().takeIf { it.isNotBlank() } ?: "Sve kategorije"
        val location = etLocation.text.toString()
        val maxPrice = seekBarPrice.progress.toDouble()

        listener?.onFiltersApplied(category, location, maxPrice)
        dismiss()
    }
}
