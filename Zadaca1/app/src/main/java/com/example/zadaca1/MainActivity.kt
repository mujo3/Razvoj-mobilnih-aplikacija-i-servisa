package com.example.zadaca1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val developers = listOf(
            BackendDev("Amila", 23, "Bosna i Hercegovina", listOf("Kotlin"), "Spring Boot"),
            BackendDev("Ibrahim", 23, "Bosna i Hercegovina", listOf("Java"), "Spring"),
            FrontendDev("Emina", 24, "Bosna i Hercegovina", listOf("Kotlin"), "React"),
            FrontendDev("Mujo", 22, "Bosna i Hercegovina", listOf("JavaScript"), "Vue.js"),
            BackendDev("Edin", 23, "Bosna i Hercegovina", listOf("Kotlin"), "Ktor")
        )
        DevFunctions.printDeveloperData(developers)
    }
}