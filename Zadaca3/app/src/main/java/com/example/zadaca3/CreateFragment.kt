package com.example.zadaca3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.example.zadaca3.databinding.FragmentCreateBinding
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.util.*

class CreateFragment : Fragment() {
    private lateinit var binding: FragmentCreateBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateBinding.inflate(inflater, container, false)
        binding.fragment = this
        return binding.root
    }

    fun saveNote() {
        val title = binding.editNoteTitle.text.toString()
        val content = binding.editNoteContent.text.toString()

        if (title.isBlank()) {
            Toast.makeText(requireContext(), "The note can't be saved without a title.", Toast.LENGTH_SHORT).show()
            return
        }

        val newNote = Note(
            id = (activity as MainActivity).notes.size + 1,
            title = title,
            content = content,
            lastModified = Date()
        )

        val notes = (activity as MainActivity).notes
        notes.add(newNote)


        notes.sortByDescending { it.lastModified }

        (activity as MainActivity).lifecycleScope.launch {
            (activity as MainActivity).saveNotes()
        }

        requireActivity().onBackPressed()
    }

    companion object {
        fun newInstance(): CreateFragment {
            return CreateFragment()
        }
    }
}
