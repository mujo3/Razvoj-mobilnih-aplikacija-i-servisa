package com.example.pronadjimajstora

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pronadjimajstora.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val serviceId = intent.getStringExtra("service_id")
    }
}