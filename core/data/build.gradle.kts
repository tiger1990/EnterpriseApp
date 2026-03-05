plugins {
    alias(libs.plugins.enterprise.android.library)
    alias(libs.plugins.kotlin.serialization)
    //alias(libs.plugins.hilt)
    //Automatically adds Hilt + Hilt Testing dependencies
    alias(libs.plugins.enterprise.android.hilt)
    //alias(libs.plugins.ksp)
    alias(libs.plugins.enterprise.android.ksp)
}

android {
    namespace = "com.enterprise.core.data"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.minSdk.get().toInt() }
}

dependencies {
    api(project(":core:domain"))
    api(project(":core:network"))

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}