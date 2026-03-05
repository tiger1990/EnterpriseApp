plugins {
    alias(libs.plugins.enterprise.android.library)
}

android {
    namespace = "com.enterprise.core.domain"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.minSdk.get().toInt() }
}

dependencies {
    api(project(":core:common"))
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.javax.inject)
}