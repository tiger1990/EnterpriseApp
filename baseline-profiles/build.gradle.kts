plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.baseline.profile)
}

android {
    namespace = "com.enterprise.baseline"
    compileSdk = 36

    // Crucial: Points to the app you are profiling
    targetProjectPath = ":app"

    defaultConfig {
        minSdk = 28  // Baseline profiles require API 28+
        // targetTestId = "com.enterprise.app"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

       // testInstrumentationRunner "androidx.benchmark.junit4.AndroidBenchmarkRunner"
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
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.profileinstaller)
    //https://developer.android.com/jetpack/androidx/releases/benchmark
    //implementation(libs.androidx.benchmark.baseline.profile.gradle.plugin)
}