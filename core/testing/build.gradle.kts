plugins {
    alias(libs.plugins.enterprise.android.library)
}

android {
    namespace = "com.enterprise.core.testing"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.minSdk.get().toInt() }
}

dependencies {
    api(project(":core:common"))
    api(project(":core:domain"))
    api(project(":core:navigation"))
    api(libs.kotlinx.coroutines.test)
    api(libs.turbine)
    api(libs.mockk)
    api(libs.junit4)
    api(libs.hilt.testing)
}