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
        setupCategorySpinner()
        setupSeekBar()
        setupApplyButton()
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
                val categories = mutableSetOf<String>().apply {
                    add("Sve kategorije")
                    result.documents.forEach { doc ->
                        doc.getString("specialization")?.let { add(it) }
                    }
                }.toList().sorted()

                ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories).also {
                    spinnerCategory.setAdapter(it)
                }
            }
    }

    private fun setupSeekBar() {
        seekBarPrice.max = 5000
        seekBarPrice.progress = 5000
        updatePriceText(5000)

        seekBarPrice.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updatePriceText(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
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