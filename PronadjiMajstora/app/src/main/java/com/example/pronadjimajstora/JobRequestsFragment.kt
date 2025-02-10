package com.example.pronadjimajstora

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pronadjimajstora.databinding.FragmentJobRequestsBinding

class JobRequestsFragment : Fragment() {
    private var _binding: FragmentJobRequestsBinding? = null
    private val binding get() = _binding!!

    // Uklonjeni su mock podaci; lista će biti popunjena pomoću Firebase/Firestore integracije.
    private val requests = mutableListOf<JobRequest>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJobRequestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val adapter = JobRequestAdapter(requests).apply {
            onRespondClick = { request ->
                // Kod klika na "Odgovori" kod majstora, izvršava se navigacija na fragment s porukama.
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, CraftsmanMessagesFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
            }
        }

        binding.rvRequests.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = JobRequestsFragment()
    }
}