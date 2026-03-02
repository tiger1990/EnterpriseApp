package convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

//refer later : https://medium.com/@amsavarthan/unlocking-reusability-in-gradle-how-to-use-kotlin-written-convention-plugins-11b95cb008ef
// ─── Android Application ─────────────────────────────────────────────────────
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("com.android.application")
        }
        extensions.configure<ApplicationExtension> {
            configureAppModule(this)
            defaultConfig.targetSdk = 35
        }
    }
}

// ─── Android Application + Compose ───────────────────────────────────────────
class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("enterprise.android.application")
            // Required for Compose 2.0+
            apply("org.jetbrains.kotlin.plugin.compose")
        }
        extensions.configure<ApplicationExtension> {
            configureCompose(this)
        }
    }
}

// ─── Android Library ─────────────────────────────────────────────────────────
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("com.android.library")
        }
        extensions.configure<LibraryExtension> {
            configureLibraryModule(this)
            // Libraries expose no resourcePrefix by default; set per-module if needed
            defaultConfig.consumerProguardFiles("consumer-rules.pro")
        }
    }
}

// ─── Android Library + Compose ───────────────────────────────────────────────
class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("enterprise.android.library")
            // Required for Compose 2.0+
            apply("org.jetbrains.kotlin.plugin.compose")
        }
        extensions.configure<LibraryExtension> {
            configureCompose(this)
        }
    }
}

// ─── Feature Module ───────────────────────────────────────────────────────────
/**
 * Every feature module gets:
 *   - Android Library + Compose
 *   - Hilt DI
 *   - Navigation
 *   - ViewModel + SavedState
 *   - Coroutines
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
        with(pluginManager) {
            apply("enterprise.android.library.compose")
            apply("enterprise.android.hilt")
            apply("org.jetbrains.kotlin.plugin.serialization")
        }

        dependencies {
            add("implementation", project(":core:ui"))
            add("implementation", project(":core:navigation"))
            add("implementation", project(":core:common"))
            add("implementation", project(":core:domain"))

            add("implementation", libs.findLibrary("androidx-lifecycle-viewmodel-compose").get())
            add("implementation", libs.findLibrary("androidx-lifecycle-runtime-compose").get())
            add("implementation", libs.findLibrary("androidx-lifecycle-viewmodel-savedstate").get())
            add("implementation", libs.findLibrary("androidx-navigation-compose").get())
            add("implementation", libs.findLibrary("hilt-navigation").get())
            add("implementation", libs.findLibrary("kotlinx-coroutines-android").get())
            add("implementation", libs.findLibrary("kotlinx-serialization-json").get())

            // Feature test dependencies
            add("testImplementation", project(":core:testing"))
            add("testImplementation", libs.findLibrary("turbine").get())
            add("testImplementation", libs.findLibrary("mockk").get())
            add("testImplementation", libs.findLibrary("kotlinx-coroutines-test").get())
            add("androidTestImplementation", project(":core:testing"))
        }
    }
}

// ─── Hilt ─────────────────────────────────────────────────────────────────────
class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
        with(pluginManager) {
            apply("com.google.dagger.hilt.android")
            apply("com.google.devtools.ksp")
        }
        dependencies {
            add("implementation", libs.findLibrary("hilt-android").get())
            add("ksp", libs.findLibrary("hilt-compiler").get())
        }
    }
}

// ─── JVM Library (domain / use-case modules with no Android deps) ─────────────
class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("org.jetbrains.kotlin.jvm")
        }
        // No Android extension; pure Kotlin
    }
}

// ─── Baseline Profile ─────────────────────────────────────────────────────────
class AndroidBaselineProfileConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
        pluginManager.apply("androidx.baselineprofile")
        dependencies {
            add("implementation", libs.findLibrary("androidx.profileinstaller").get())
        }
        // Ensure this is only applied to modules that have an Android plugin
//        pluginManager.withPlugin("com.android.application") {
//            pluginManager.apply("androidx.baselineprofile")
//            dependencies {
//                add("implementation", libs.findLibrary("androidx.profileinstaller").get())
//            }
//        }
//        pluginManager.withPlugin("com.android.library") {
//            pluginManager.apply("androidx.baselineprofile")
//            dependencies {
//                add("implementation", libs.findLibrary("androidx.profileinstaller").get())
//            }
//        }
        // Now the extension is available
//        extensions.configure<BaselineProfileProducerExtension> {
//            // Configure producer-specific options here
//            // e.g., managedDevices += "pixel6Api31"
//            warnings {
////                maxAgpVersion.set(false)
//            }
//        }
//        extensions.configure<BaselineProfileProducerExtension> {
//                    // New structure for handling AGP version strictness in 1.3.x+
//                    warnings {
//                        strict.set(false) // Replaces old maxAgpVersion toggle
//                    }
//                }
    }
}
