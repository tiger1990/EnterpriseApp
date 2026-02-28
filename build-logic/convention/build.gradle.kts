import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.enterprise.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.plugins.android.application.toDep())
    compileOnly(libs.plugins.android.library.toDep())
    compileOnly(libs.plugins.kotlin.jvm.toDep())
    compileOnly(libs.plugins.compose.compiler.toDep())
    compileOnly(libs.plugins.hilt.toDep())
    compileOnly(libs.plugins.ksp.toDep())
}

// Extension to convert PluginDependency to dependency notation
fun Provider<PluginDependency>.toDep() = map {
    "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}"
}

gradlePlugin {
    plugins {
        // Android Application
        register("androidApplication") {
            id = "enterprise.android.application"
            implementationClass = "convention.AndroidApplicationConventionPlugin"
        }
        // Android Library
        register("androidLibrary") {
            id = "enterprise.android.library"
            implementationClass = "convention.AndroidLibraryConventionPlugin"
        }
        // Android Library with Compose
        register("androidLibraryCompose") {
            id = "enterprise.android.library.compose"
            implementationClass = "convention.AndroidLibraryComposeConventionPlugin"
        }
        // Android Application with Compose
        register("androidApplicationCompose") {
            id = "enterprise.android.application.compose"
            implementationClass = "convention.AndroidApplicationComposeConventionPlugin"
        }
        // Feature module (Library + Compose + Hilt + Nav)
        register("androidFeature") {
            id = "enterprise.android.feature"
            implementationClass = "convention.AndroidFeatureConventionPlugin"
        }
        // Hilt
        register("androidHilt") {
            id = "enterprise.android.hilt"
            implementationClass = "convention.AndroidHiltConventionPlugin"
        }
        // JVM Library
        register("jvmLibrary") {
            id = "enterprise.jvm.library"
            implementationClass = "convention.JvmLibraryConventionPlugin"
        }
        // Baseline Profile
        register("androidBaselineProfile") {
            id = "enterprise.android.application.baselineprofile"
            implementationClass = "convention.AndroidBaselineProfileConventionPlugin"
        }
    }
}
