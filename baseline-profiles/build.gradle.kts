plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.baseline.profile)
    alias(libs.plugins.enterprise.android.ksp)
}

android {
    namespace = "com.enterprise.baseline"
    compileSdk = libs.versions.compileSdk.get().toInt()

    // Crucial: Points to the app you are profiling
    targetProjectPath = ":app"

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()  // Baseline profiles require API 28+

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

// BaselineProfileRule runs on a connected device/emulator and captures hot
// method traces that the compiler uses to AOT-compile critical paths.
baselineProfile {
    // Note: mergeIntoMain and automaticGenerationDuringBuild should be configured 
    // in the consumer module (e.g., :app or library module), not here in the producer module.
}

dependencies {
    implementation(libs.androidx.junit)
    implementation(libs.androidx.espresso)
    implementation(libs.androidx.baseline.profile)
    implementation(libs.androidx.benchmark.macro)
    implementation(libs.androidx.uiautomator)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.profileinstaller)
    //https://developer.android.com/jetpack/androidx/releases/benchmark
    //implementation(libs.androidx.benchmark.baseline.profile.gradle.plugin)
}