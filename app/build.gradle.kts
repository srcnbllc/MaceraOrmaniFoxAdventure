plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.zekaoformani.macera"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.zekaoformani.macera"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core (Anayasa Madde 1)
    implementation(libs.androidx.core.ktx)
    implementation(libs.jetbrains.lifecycle.runtime.compose)
    implementation(libs.jetbrains.lifecycle.viewmodel.compose)
    
    // UI & Compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.splashscreen)
    
    // Shared Module
    implementation(project(":shared"))
    
    // Navigation
    implementation(libs.jetbrains.navigation.compose)

    // DataStore & Others
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
}
