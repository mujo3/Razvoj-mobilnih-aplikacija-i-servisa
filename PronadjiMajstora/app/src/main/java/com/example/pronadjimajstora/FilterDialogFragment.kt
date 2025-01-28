package com.example.pronadjimajstora

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import android.widget.SeekBar

class FilterDialogFragment : DialogFragment() {

    interface FilterListener {
        fun onFiltersApplied(category: String, location: String, maxPrice: Int)
    }

    private var listener: FilterListener? = null

    private lateinit var spinnerCategory: MaterialAutoCompleteTextView
    private lateinit var etLocation: TextInputEditText
    private lateinit var seekBarPrice: SeekBar
    private lateinit var btnApplyFilters: MaterialButton

    fun setFilterListener(listener: FilterListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate koristi originalni layout
        return inflater.inflate(R.layout.dialog_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicijalizacija UI komponenti
        spinnerCategory = view.findViewById(R.id.spinnerCategory)
        etLocation = view.findViewById(R.id.etLocation)
        seekBarPrice = view.findViewById(R.id.seekBarPrice)
        btnApplyFilters = view.findViewById(R.id.btnApplyFilters)

        setupCategorySpinner()
        setupSeekBar()

        btnApplyFilters.setOnClickListener {
            applyFilters()
        }
    }

    private fun setupCategorySpinner() {
        val categories = listOf(
            "Sve kategorije",
            "Građevinski radovi",
            "Električarski radovi",
            "Vodoinstalaterski radovi",
            "Stolarski radovi"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            categories
        )

        spinnerCategory.setAdapter(adapter)
        spinnerCategory.setOnItemClickListener { _, _, position, _ ->
            spinnerCategory.setText(categories[position])
            spinnerCategory.contentDescription = "Odabrana kategorija: ${categories[position]}"
        }
    }

    private fun setupSeekBar() {
        seekBarPrice.max = 10000
        seekBarPrice.progress = 5000
        seekBarPrice.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun applyFilters() {
        val selectedCategory = spinnerCategory.text.toString()
        val location = etLocation.text.toString()
        val maxPrice = seekBarPrice.progress

        listener?.onFiltersApplied(
            if (selectedCategory.isEmpty()) "Sve kategorije" else selectedCategory,
            location,
            if (maxPrice == 0) 10000 else maxPrice
        )
        dismiss()
    }
}
