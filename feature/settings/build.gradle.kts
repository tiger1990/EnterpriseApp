plugins {
    alias(libs.plugins.enterprise.android.feature)
    alias(libs.plugins.paparazzi)
    alias(libs.plugins.enterprise.android.ksp)
}

android {
    namespace = "com.enterprise.feature.settings"
}