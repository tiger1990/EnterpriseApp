plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.enterprise.core.domain"
    compileSdk = 36
    defaultConfig { minSdk = 26 }
}

dependencies {
    api(project(":core:common"))
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.javax.inject)
}