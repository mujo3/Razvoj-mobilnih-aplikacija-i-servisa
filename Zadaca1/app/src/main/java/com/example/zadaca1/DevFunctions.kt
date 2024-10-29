package com.example.zadaca1

object DevFunctions { //koristimo Object kako bismo implementirali i grupisali sve potrebne funkcije

    fun countDevelopersByLanguageUsingGroupingBy(developers: List<Developer>): Map<String, Int> {
        return developers
            .flatMap { it.getLanguages() }
            .groupingBy { it }
            .eachCount()
    }

    fun averageAgeByLanguageUsingGroupingBy(developers: List<Developer>): Map<String, Double> {
        return developers
            .flatMap { developer -> developer.getLanguages().map { language -> language to developer.getAge() } }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, ages) -> ages.average() }
    }

    fun countDevelopersByLanguageWithoutGroupingBy(developers: List<Developer>): Map<String, Int> {
        val languageCount = mutableMapOf<String, Int>()
        for (developer in developers) {
            for (language in developer.getLanguages()) {
                languageCount[language] = languageCount.getOrDefault(language, 0) + 1
            }
        }
        return languageCount
    }


    fun averageAgeByLanguageWithoutGroupingBy(developers: List<Developer>): Map<String, Double> {
        val ageSumByLanguage = mutableMapOf<String, Int>()
        val countByLanguage = mutableMapOf<String, Int>()

        for (developer in developers) {
            for (language in developer.getLanguages()) {
                ageSumByLanguage[language] = ageSumByLanguage.getOrDefault(language, 0) + developer.getAge()
                countByLanguage[language] = countByLanguage.getOrDefault(language, 0) + 1
            }
        }

        return ageSumByLanguage.mapValues { (language, ageSum) ->
            ageSum.toDouble() / countByLanguage[language]!!
        }
    }

    fun printDeveloperData(developers: List<Developer>) {
        for (developer in developers) {

            println("Ime i prezime: ${developer.getName()}")

            when (developer) {
                is BackendDev -> {
                    println("Tip: Backend Developer")
                    println("Jezici: ${developer.getLanguages().joinToString(", ")}")
                    println("Framework: ${developer.backendFramework}")
                }
                is FrontendDev -> {
                    println("Tip: Frontend Developer")
                    println("Jezici: ${developer.getLanguages().joinToString(", ")}")
                    println("Framework: ${developer.frontendFramework}")
                }
            }
            println("/////////////////////////")
        }
    }

}