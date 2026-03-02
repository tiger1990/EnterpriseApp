plugins {
    alias(libs.plugins.enterprise.android.feature)
    id("app.cash.paparazzi")
}

android {
    namespace = "com.enterprise.feature.detail"
}