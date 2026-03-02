plugins {
    alias(libs.plugins.enterprise.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.enterprise.android.hilt)
}

android {
    namespace = "com.enterprise.core.network"
    compileSdk = 36
    defaultConfig { minSdk = 26 }
}

dependencies {
    api(libs.retrofit.core)
    api(libs.retrofit.serialization)
    api(libs.okhttp.core)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)
}