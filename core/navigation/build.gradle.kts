plugins {
    alias(libs.plugins.enterprise.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.enterprise.android.hilt)
    alias(libs.plugins.enterprise.android.ksp)
}

android {
    namespace = "com.enterprise.core.navigation"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.minSdk.get().toInt() }
}

dependencies {
    //implementation(libs.androidx.navigation3.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.kotlinx.coroutines.android)
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.navigation3.runtime)
    api(libs.androidx.navigation3.ui)
    implementation(libs.androidx.compose.material3)

    testImplementation(libs.junit4)
    testImplementation(libs.robolectric)
}