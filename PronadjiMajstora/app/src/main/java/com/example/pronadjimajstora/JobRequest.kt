package com.example.pronadjimajstora

import java.util.Date

data class JobRequest(
    val id: String = "",
    val customerId: String = "",
    val description: String = "",
    val budget: Double = 0.0,
    val location: String = "",
    var status: String = "Na čekanju",
    val timestamp: Date = Date()
)
