package com.example.zadaca3

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.zadaca3.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val notes = mutableListOf<Note>()
    private val notesFileName = "notes.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        lifecycleScope.launch {
            loadNotes()
            openMainFragment()
        }

        val infoIcon: ImageView = binding.infoIcon
        infoIcon.setOnClickListener {
            openAboutFragment()
        }

        val profileIcon: ImageView = binding.profileIcon
        profileIcon.setOnClickListener {
            openMainFragment()
        }
    }

    private fun openAboutFragment() {
        val fragment = AboutFragment()
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun openMainFragment() {
        val fragment = MainFragment()
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()


        fragment.refreshNotes()
    }

    override fun onPause() {
        super.onPause()

        lifecycleScope.launch {
            saveNotes()
        }
    }

    private suspend fun loadNotes() {
        withContext(Dispatchers.IO) {
            val file = File(filesDir, notesFileName)


            if (!file.exists()) {
                file.writeText("[]")
            }

            val json = file.readText()
            val type = object : TypeToken<MutableList<Note>>() {}.type
            val loadedNotes: List<Note> = Gson().fromJson(json, type) ?: emptyList()


            withContext(Dispatchers.Main) {
                notes.clear()
                notes.addAll(loadedNotes)
                notes.sortByDescending { it.lastModified }
            }
        }
    }

    internal suspend fun saveNotes() {
        withContext(Dispatchers.IO) {
            val file = File(filesDir, notesFileName)
            val json = Gson().toJson(notes)
            file.writeText(json)
        }
    }
}
