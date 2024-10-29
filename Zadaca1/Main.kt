

fun main(){

        val developers = listOf(
            BackendDev("Amila", 23, "Bosna i Hercegovina", listOf("Kotlin"), "Spring Boot"),
            BackendDev("Ibrahim", 23, "Bosna i Hercegovina", listOf("Java"), "Spring"),
            FrontendDev("Emina", 24, "Bosna i Hercegovina", listOf("Kotlin"), "React"),
            FrontendDev("Mujo", 22, "Bosna i Hercegovina", listOf("JavaScript"), "Vue.js"),
            BackendDev("Edin", 23, "Bosna i Hercegovina", listOf("Kotlin"), "Ktor")
        )
        DevFunctions.printDeveloperData(developers)
}