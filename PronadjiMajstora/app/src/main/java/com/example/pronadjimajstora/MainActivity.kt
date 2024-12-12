package com.example.pronadjimajstora

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var mList: ArrayList<OffersData>
    private lateinit var adapter: OffersAdapter
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById(R.id.searchView)
        loginButton = findViewById(R.id.loginButton)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        mList = ArrayList() 
        addDataToList()

        adapter = OffersAdapter(mList)
        recyclerView.adapter = adapter

        setupSearchView()

        loginButton.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun addDataToList() {
        mList.add(OffersData("Autoserviser", R.drawable.autoserviser))
        mList.add(OffersData("Bravar", R.drawable.bravar))
        mList.add(OffersData("Električar", R.drawable.elektricar))
        mList.add(OffersData("Keramičar", R.drawable.keramicar))
        mList.add(OffersData("Moler", R.drawable.moler))
        mList.add(OffersData("Stolar", R.drawable.stolar))
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {

                    adapter.updateList(mList)
                } else {

                    val filteredList = mList.filter {
                        it.title.contains(newText, ignoreCase = true)
                    }
                    adapter.updateList(filteredList)
                }
                return true
            }
        })
    }




}
