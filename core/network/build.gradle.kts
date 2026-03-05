plugins {
    alias(libs.plugins.enterprise.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.enterprise.android.hilt)
    alias(libs.plugins.enterprise.android.ksp)
}

android {
    namespace = "com.enterprise.core.network"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.minSdk.get().toInt() }
}

dependencies {
    api(libs.retrofit.core)
    api(libs.retrofit.serialization)
    api(libs.okhttp.core)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)
}