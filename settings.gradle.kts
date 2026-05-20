pluginManagement {
    repositories {
        google() // Filtreleri kaldırıp temizce Google deposunu tanımlıyoruz
        mavenCentral() // JetBrains eklentileri asıl buradan beslenir
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") // JetBrains resmi Compose deposu
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "MaceraOrmaniFoxAdventure"
include(":app")
include(":shared")