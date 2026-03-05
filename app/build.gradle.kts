plugins {
    alias(libs.plugins.enterprise.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.enterprise.android.application.compose)
    //Automatically adds Hilt + Hilt Testing dependencies
    alias(libs.plugins.enterprise.android.hilt)
    alias(libs.plugins.enterprise.android.application.baselineprofile)
    alias(libs.plugins.enterprise.android.ksp)
}

android {
    namespace = "com.enterprise.app"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.enterprise.app"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        // Enable deep link intent filters
        manifestPlaceholders["appDeepLinkScheme"] = "enterprise"

        testInstrumentationRunner = "com.enterprise.app.testing.HiltTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file("myreleasekey.keystore")
            storePassword = System.getenv("KSTOREPWD")
            keyAlias = "MyReleaseKey"
            keyPassword = System.getenv("KEYPWD")
        }
    }

    //https://developer.android.com/build/build-variants#filter-variants
    buildTypes {
        debug {
            applicationIdSuffix  = ".debug"
            versionNameSuffix    = "-debug"
            isDebuggable         = true
        }
        release {
            isMinifyEnabled      = true
            isShrinkResources    = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

//    sourceSets {
//        getByName("debug") {
//            // AGP handles the merging of Kotlin and Java sources here
//            kotlin.directories.add("build/generated/ksp/debug/kotlin")
//            java.directories.add("build/generated/ksp/debug/java")
//        }
//        getByName("release") {
//            // AGP handles the merging of Kotlin and Java sources here
//            kotlin.directories.add("build/generated/ksp/debug/kotlin")
//            java.directories.add("build/generated/ksp/debug/java")
//        }
//    }
}

// Baseline profile DSL
baselineProfile {
    // Merges profiles from all variants into src/main/generated/baselineProfiles
    mergeIntoMain = true
    automaticGenerationDuringBuild = false
}

dependencies {
    // Feature modules
    implementation(project(":feature:home"))
    implementation(project(":feature:detail"))
    implementation(project(":feature:search"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:settings"))

    // Core
    implementation(project(":core:navigation"))
    implementation(project(":core:data"))       // Provides DI bindings
    implementation(project(":core:ui"))
    implementation(project(":core:common"))

    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    testImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso)
    androidTestImplementation(libs.androidx.compose.ui.test)

    // Required to install profiles on devices without Play Services
    implementation(libs.androidx.profileinstaller)

    // Link the app to the producer module --Baseline
    "baselineProfile"(project(":baseline-profiles"))
}
