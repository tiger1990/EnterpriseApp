plugins {
    alias(libs.plugins.enterprise.android.library.compose)
}

android {
    namespace = "com.enterprise.core.ui"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.minSdk.get().toInt() }
}

dependencies {
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.animation)
    api(project(":core:tokens"))
}