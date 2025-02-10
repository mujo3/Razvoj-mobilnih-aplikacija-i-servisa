buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.2")
    }
}
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false // Updated Kotlin version
    id("com.google.gms.google-services") version "4.4.0" apply false // Ensure correct plugin version
}