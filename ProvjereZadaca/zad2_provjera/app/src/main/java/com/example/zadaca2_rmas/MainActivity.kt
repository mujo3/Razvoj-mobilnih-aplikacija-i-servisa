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
    private lateinit var brightnessSeekBar: SeekBar

    private lateinit var redValueText: TextView
    private lateinit var greenValueText: TextView
    private lateinit var blueValueText: TextView
    private lateinit var brightnessValueText: TextView

    private lateinit var hexValueText: TextView
    private lateinit var colorDisplay: View
    private lateinit var colorLabel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        redSeekBar = findViewById(R.id.redSeekBar)
        greenSeekBar = findViewById(R.id.greenSeekBar)
        blueSeekBar = findViewById(R.id.blueSeekBar)
        brightnessSeekBar = findViewById(R.id.osvjetljenjeSeekBar)


        redValueText = findViewById(R.id.redValueText)
        greenValueText = findViewById(R.id.greenValueText)
        blueValueText = findViewById(R.id.blueValueText)
        brightnessValueText = findViewById(R.id.osvjetljenjeValueText)


        hexValueText = findViewById(R.id.hexValueText)
        colorDisplay = findViewById(R.id.colorDisplay)


        colorLabel = findViewById(R.id.colorLabel)


        colorDisplay.setBackgroundColor(Color.BLACK)


        redSeekBar.setOnSeekBarChangeListener(colorChangeListener)
        greenSeekBar.setOnSeekBarChangeListener(colorChangeListener)
        blueSeekBar.setOnSeekBarChangeListener(colorChangeListener)
        brightnessSeekBar.setOnSeekBarChangeListener(colorChangeListener)


        updateColorView()
    }


    private val colorChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            updateColorView()
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }


    private fun updateColorView() {
        val red = redSeekBar.progress
        val green = greenSeekBar.progress
        val blue = blueSeekBar.progress
        val brightness = brightnessSeekBar.progress

        //dodane nove varijable koje mijenjaju trenutnu boju po formuli zadanoj u zadatku
        val adjustedRed = (red * brightness / 100.0).toInt()
        val adjustedGreen = (green * brightness / 100.0).toInt()
        val adjustedBlue = (blue * brightness / 100.0).toInt()


        redValueText.text = red.toString()
        greenValueText.text = green.toString()
        blueValueText.text = blue.toString()
        brightnessValueText.text = "$brightness%" //dodana vrijednost osvjetljenja


        val color = Color.rgb(adjustedRed, adjustedGreen, adjustedBlue)
        colorDisplay.setBackgroundColor(color)


        val hexValue = String.format("#%02X%02X%02X", adjustedRed, adjustedGreen, adjustedBlue)
        hexValueText.text = "HEX Color: $hexValue"
        colorLabel.text = "Izabrana boja je: $hexValue, Osvjetljenje: $brightness%" //promijenjen ispis kako bi dodao i osvjetljenje
    }
}
