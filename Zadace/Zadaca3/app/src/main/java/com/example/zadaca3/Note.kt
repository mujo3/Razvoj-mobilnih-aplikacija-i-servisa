package com.example.zadaca3

import java.util.Date

data class Note(
    var id: Int,
    var title: String,
    var content: String,
    var lastModified: Date
)
