package convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

@Suppress("unused")
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