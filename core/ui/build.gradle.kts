plugins {
    alias(libs.plugins.enterprise.android.library.compose)
}

android {
    namespace = "com.enterprise.core.ui"
    compileSdk = 36
    defaultConfig { minSdk = 26 }
}

dependencies {
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.animation)
}