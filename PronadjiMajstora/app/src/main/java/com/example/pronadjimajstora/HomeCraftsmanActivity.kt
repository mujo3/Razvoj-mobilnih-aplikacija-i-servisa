package com.example.pronadjimajstora

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.pronadjimajstora.databinding.ActivityHomeCraftsmanBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeCraftsmanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeCraftsmanBinding
    private lateinit var viewPagerAdapter: CraftsmanViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeCraftsmanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupBottomNavigation()
    }

    private fun setupViewPager() {
        viewPagerAdapter = CraftsmanViewPagerAdapter(this)
        binding.viewPager.apply {
            adapter = viewPagerAdapter
            isUserInputEnabled = false
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    binding.bottomNavigation.menu.getItem(position).isChecked = true
                }
            })
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_profile -> binding.viewPager.currentItem = 0
                R.id.nav_requests -> binding.viewPager.currentItem = 1
                R.id.nav_add_service -> binding.viewPager.currentItem = 2
                R.id.nav_messages -> binding.viewPager.currentItem = 3
            }
            true
        }
    }

    private inner class CraftsmanViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 4

        override fun createFragment(position: Int): Fragment {
            return when(position) {
                0 -> CraftsmanProfileFragment.newInstance()
                1 -> JobRequestsFragment.newInstance()
                2 -> AddServiceFragment.newInstance()
                3 -> CraftsmanMessagesFragment.newInstance()
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }
    }
}