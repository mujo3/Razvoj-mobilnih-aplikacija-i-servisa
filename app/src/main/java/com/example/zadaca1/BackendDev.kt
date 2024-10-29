package com.example.zadaca1

class BackendDev(
     name: String,
     age: Int,
     country: String,
     languages: List<String>,
    val backendFramework: String
    ): Developer(name, age, country, languages) {}