package com.example.zadaca2_rmas

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var redSeekBar: SeekBar
    private lateinit var greenSeekBar: SeekBar
    private lateinit var blueSeekBar: SeekBar

    private lateinit var redValueText: TextView
    private lateinit var greenValueText: TextView
    private lateinit var blueValueText: TextView

    private lateinit var hexValueText: TextView
    private lateinit var colorDisplay: View
    private lateinit var colorLabel: TextView // Dodano

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize SeekBars
        redSeekBar = findViewById(R.id.redSeekBar)
        greenSeekBar = findViewById(R.id.greenSeekBar)
        blueSeekBar = findViewById(R.id.blueSeekBar)

        // Initialize Value TextViews
        redValueText = findViewById(R.id.redValueText)
        greenValueText = findViewById(R.id.greenValueText)
        blueValueText = findViewById(R.id.blueValueText)

        // Initialize Hex Color TextView and the color display view
        hexValueText = findViewById(R.id.hexValueText)
        colorDisplay = findViewById(R.id.colorDisplay)

        // Initialize the color label TextView
        colorLabel = findViewById(R.id.colorLabel) // Dodato

        // Set the initial background color of the colorDisplay
        colorDisplay.setBackgroundColor(Color.BLACK)

        // Set up listeners for each SeekBar
        redSeekBar.setOnSeekBarChangeListener(colorChangeListener)
        greenSeekBar.setOnSeekBarChangeListener(colorChangeListener)
        blueSeekBar.setOnSeekBarChangeListener(colorChangeListener)

        // Initialize the color display
        updateColorView()
    }

    // Listener to handle SeekBar changes
    private val colorChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            updateColorView()
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }

    // Function to update the color display based on SeekBar values
    private fun updateColorView() {
        val red = redSeekBar.progress
        val green = greenSeekBar.progress
        val blue = blueSeekBar.progress

        // Update the RGB value TextViews
        redValueText.text = red.toString()
        greenValueText.text = green.toString()
        blueValueText.text = blue.toString()

        // Update the colorDisplay background color
        val color = Color.rgb(red, green, blue)
        colorDisplay.setBackgroundColor(color)

        // Format and set the HEX value in the TextView
        val hexValue = String.format("#%02X%02X%02X", red, green, blue)
        hexValueText.text = "HEX Color: $hexValue"

        // Update the colorLabel to show the selected color
        colorLabel.text = "Izabrana boja je: $hexValue" // Dodato
    }
}