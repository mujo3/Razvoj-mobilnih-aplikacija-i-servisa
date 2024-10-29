package com.example.zadaca1

open class Developer( //koristimo open class jer su klase po defaultu u Kotlinu finalne i ne mogu se nasljedjivati
    private val name: String,
    private val age: Int,
    private val country: String,
    private val languages: List<String>
) : Person { //klasa nasljedjuje interfejs
    override fun getAge(): Int {
        return age
    }

    override fun getCountry(): String {
        return country
    }

    fun getName(): String {
        return name
    }

    fun getLanguages(): List<String> {
        return languages
    }
}