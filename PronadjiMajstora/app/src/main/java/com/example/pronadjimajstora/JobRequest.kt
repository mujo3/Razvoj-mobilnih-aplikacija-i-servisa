package com.example.pronadjimajstora

import java.util.Date

data class JobRequest(
    val id: String = "",
    val customerId: String = "",
    val description: String = "",
    val budget: Double = 0.0,
    val location: String = "",
    val status: String = "pending",
    val timestamp: Date = Date()
)