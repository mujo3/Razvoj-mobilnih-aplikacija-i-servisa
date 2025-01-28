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

        // Mock podaci
        val mockRequests = listOf(
            JobRequest(description = "Popravak slavine", budget = 50.0),
            JobRequest(description = "Instalacija bojlera", budget = 200.0)
        )

        binding.rvRequests.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = JobRequestAdapter(mockRequests)
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