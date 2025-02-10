package com.example.pronadjimajstora

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.pronadjimajstora.databinding.ActivityHomeCraftsmanBinding

class HomeCraftsmanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeCraftsmanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeCraftsmanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Postavi početni fragment na profil majstora
        loadFragment(CraftsmanProfileFragment.newInstance())

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_profile -> loadFragment(CraftsmanProfileFragment.newInstance())
                R.id.nav_requests -> loadFragment(JobRequestsFragment.newInstance())
                R.id.nav_add_service -> loadFragment(AddServiceFragment.newInstance())
                R.id.nav_messages -> loadFragment(CraftsmanMessagesFragment.newInstance())
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
