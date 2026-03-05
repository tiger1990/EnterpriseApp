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
    // Standard AGP and Kotlin plugins, These allow the convention plugin to "see" the Android and KSP classes
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)

    // Plugin classes for Extension Visibility
    // We use implementation for the baseline profile plugin to see the ProducerExtension class
    // Add this so your convention plugin can see the Baseline Profile classes
    implementation(libs.plugins.baseline.profile.toDep())
    // Add this so your convention plugin can see the Baseline Profile classes
    implementation(libs.androidx.benchmark.baseline.profile.gradle.plugin)

     compileOnly(libs.plugins.compose.compiler.toDep())
     compileOnly(libs.plugins.hilt.toDep())

//    compileOnly(libs.plugins.android.application.toDep())
//    compileOnly(libs.plugins.android.library.toDep())
//    compileOnly(libs.plugins.android.test.toDep())
//    compileOnly(libs.plugins.kotlin.jvm.toDep())
//    compileOnly(libs.plugins.ksp.toDep())

//    compileOnly(libs.detekt.gradlePlugin)
//    compileOnly(libs.apollo.gradlePlugin)

//    implementation(libs.com.android.compose.screenshot.gradle.plugin) {
//        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
//        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk7")
//        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
//        exclude(group = "org.jetbrains.kotlin", module = "kotlin-reflect")
//    }
//
//    testImplementation(libs.junit)
//    testImplementation(libs.assertk)
//    testImplementation(gradleTestKit())
//    implementation(libs.jacoco.core)
//    implementation(libs.org.sonarsource.scanner.gradle)
}

// Extension to convert PluginDependency to dependency notation
fun Provider<PluginDependency>.toDep() = map {
    "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}"
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
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
        register("androidKsp") {
            id = "enterprise.android.ksp"
            implementationClass = "convention.AndroidKspConventionPlugin"
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
