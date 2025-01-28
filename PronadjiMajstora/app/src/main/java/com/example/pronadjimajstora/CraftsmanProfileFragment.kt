package com.example.pronadjimajstora

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pronadjimajstora.databinding.FragmentCraftsmanProfileBinding

class CraftsmanProfileFragment : Fragment() {
    private var _binding: FragmentCraftsmanProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCraftsmanProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMockData()
    }

    private fun setupMockData() {
        // Testni podaci za majstora
        binding.textView.text = "Marko Marković\nSpecijalizacija: Vodoinstalater\nOcjena: ★★★★☆"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = CraftsmanProfileFragment()
    }
}