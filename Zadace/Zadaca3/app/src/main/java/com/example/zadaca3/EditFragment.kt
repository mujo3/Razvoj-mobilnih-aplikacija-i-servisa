package com.example.zadaca3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.zadaca3.databinding.FragmentEditBinding
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.util.*

class EditFragment : Fragment() {
    private lateinit var binding: FragmentEditBinding
    private var currentNote: Note? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditBinding.inflate(inflater, container, false)
        binding.fragment = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val noteJson = arguments?.getString("note_json")
        if (noteJson != null) {
            currentNote = Gson().fromJson(noteJson, Note::class.java)
            currentNote?.let {
                binding.editNoteTitle.setText(it.title)
                binding.editNoteContent.setText(it.content)
            }
        }

        binding.saveNoteButton.setOnClickListener {
            saveNote()
        }
    }

    fun saveNote() {
        val title = binding.editNoteTitle.text.toString()
        val content = binding.editNoteContent.text.toString()

        if (title.isBlank()) {
            Toast.makeText(requireContext(), "The note can't be saved without a title.", Toast.LENGTH_SHORT).show()
            return
        }

        currentNote?.let { note ->
            note.title = title
            note.content = content
            note.lastModified = Date()

            val notes = (activity as MainActivity).notes
            val index = notes.indexOfFirst { it.id == note.id }
            if (index != -1) {
                notes[index] = note
            }


            notes.sortByDescending { it.lastModified }


            (activity as MainActivity).lifecycleScope.launch {
                (activity as MainActivity).saveNotes()
            }


            requireActivity().onBackPressed()
        }
    }


    companion object {
        fun newInstance(note: Note): EditFragment {
            val fragment = EditFragment()
            val args = Bundle()
            val noteJson = Gson().toJson(note)
            args.putString("note_json", noteJson)
            fragment.arguments = args
            return fragment
        }
    }
}
