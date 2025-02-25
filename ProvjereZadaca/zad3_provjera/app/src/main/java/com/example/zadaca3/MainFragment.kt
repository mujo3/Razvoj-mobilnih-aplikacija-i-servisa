package com.example.zadaca3

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zadaca3.databinding.FragmentMainBinding
import kotlinx.coroutines.launch

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private val notesAdapter: NoteAdapter by lazy {
        NoteAdapter((activity as MainActivity).notes,
            onNoteClick = { it?.let { openEditFragment(it) } },
            onNoteLongClick = { it?.let { showDeleteConfirmationDialog(it) } })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.notesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.notesRecyclerView.adapter = notesAdapter

        binding.addNoteFab.setOnClickListener {
            openCreateFragment()
        }

        refreshNotes()
        updateNotesCount()
    }

    fun refreshNotes() {
        if (::binding.isInitialized) {
            binding.notesRecyclerView.adapter?.notifyDataSetChanged()
            updateNotesCount()
        }
    }

    private fun updateNotesCount() {
        val notesCount = (activity as MainActivity).notes.size
        binding.notesCountTV.text = "Broj zabilješki: $notesCount"
    }

    private fun openCreateFragment() {
        val fragment = CreateFragment.newInstance()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun openEditFragment(note: Note) {
        val fragment = EditFragment.newInstance(note)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showDeleteConfirmationDialog(note: Note) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Note")
        builder.setMessage("Are you sure you want to delete this note?")
        builder.setPositiveButton("Yes") { _, _ ->
            (activity as MainActivity).notes.remove(note)
            notesAdapter.notifyDataSetChanged()
            updateNotesCount()
            lifecycleScope.launch {
                (activity as MainActivity).saveNotes()
            }
        }
        builder.setNegativeButton("No", null)
        builder.show()
    }
}
