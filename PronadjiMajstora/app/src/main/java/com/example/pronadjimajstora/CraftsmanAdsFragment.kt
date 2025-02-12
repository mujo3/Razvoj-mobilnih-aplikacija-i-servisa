package com.example.pronadjimajstora

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pronadjimajstora.databinding.FragmentCraftsmanAdsBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CraftsmanAdsFragment : Fragment() {
    private var _binding: FragmentCraftsmanAdsBinding? = null
    private val binding get() = _binding!!

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var craftsmanName: String? = null

    private lateinit var adapter: CraftsmanAdAdapter
    private var ads = mutableListOf<Service>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCraftsmanAdsBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        fetchCurrentUserDataAndLoadAds()
    }

    private fun setupRecyclerView() {
        adapter = CraftsmanAdAdapter(ads,
            onEditClick = { service ->
                // Navigacija na fragment za uređivanje oglasa
                val editFragment = EditServiceFragment.newInstance(service.id)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, editFragment)
                    .addToBackStack(null)
                    .commit()
            },
            onDeleteClick = { service ->
                confirmAndDeleteAd(service)
            }
        )
        binding.rvCraftsmanAds.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCraftsmanAds.adapter = adapter
    }

    private fun fetchCurrentUserDataAndLoadAds() {
        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid == null) {
            Snackbar.make(binding.root, "Korisnik nije prijavljen", Snackbar.LENGTH_LONG).show()
            return
        }
        firestore.collection("users").document(currentUserUid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    craftsmanName = document.getString("name")
                    loadAds()
                } else {
                    Snackbar.make(binding.root, "Korisnički podaci nisu dostupni", Snackbar.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Snackbar.make(binding.root, "Greška pri dohvaćanju korisničkih podataka: ${e.message}", Snackbar.LENGTH_LONG).show()
            }
    }

    private fun loadAds() {
        craftsmanName?.let { name ->
            firestore.collection("services")
                .whereEqualTo("craftsman", name)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Snackbar.make(binding.root, "Greška: ${error.message}", Snackbar.LENGTH_LONG).show()
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        ads.clear()
                        for (doc in snapshot.documents) {
                            val service = doc.toObject(Service::class.java)
                            if (service != null) {
                                ads.add(service)
                            }
                        }
                        adapter.updateList(ads)
                    }
                }
        }
    }

    private fun confirmAndDeleteAd(service: Service) {
        AlertDialog.Builder(requireContext())
            .setTitle("Obriši oglas")
            .setMessage("Jeste li sigurni da želite obrisati ovaj oglas?")
            .setPositiveButton("Da") { dialog, _ ->
                deleteAd(service)
                dialog.dismiss()
            }
            .setNegativeButton("Ne") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteAd(service: Service) {
        firestore.collection("services").document(service.id)
            .delete()
            .addOnSuccessListener {
                Snackbar.make(binding.root, "Oglas obrisan", Snackbar.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Snackbar.make(binding.root, "Greška: ${e.message}", Snackbar.LENGTH_LONG).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = CraftsmanAdsFragment()
    }
}
