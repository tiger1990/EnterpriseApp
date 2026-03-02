plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.enterprise.core.common"
    compileSdk = 36
    defaultConfig { minSdk = 26 }
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
}