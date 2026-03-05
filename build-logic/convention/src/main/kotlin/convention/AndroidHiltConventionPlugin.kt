package convention

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByType

@Suppress("unused")
// ─── Hilt ─────────────────────────────────────────────────────────────────────
class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
        // Apply Plugins
        with(pluginManager) {
            apply("com.google.dagger.hilt.android")
            apply("com.google.devtools.ksp")
        }

        // Inside your apply block
        val androidComponents = extensions.findByType<AndroidComponentsExtension<*, *, *>>()
        androidComponents?.onVariants { variant ->
            // 1. Define the path where KSP puts its generated files for this variant
            val kspGeneratedDir = layout.buildDirectory.dir("generated/ksp/${variant.name}/kotlin")

            // 2. Register the directory as a static source for this specific variant
            // This covers 'debug', 'release', and 'benchmarkRelease' automatically
            variant.sources.kotlin?.addStaticSourceDirectory(
                kspGeneratedDir.map { it.asFile.absolutePath }.toString()
            )
        }

        // Configure Dependencies
        dependencies {
            // Main Code
            add("implementation", libs.findLibrary("hilt-android").get())
            add("ksp", libs.findLibrary("hilt-compiler").get())

            // --- ADD THESE FOR Instrumented Tests ---
            // Allows HiltTestRunner to find HiltTestApplication
            add("androidTestImplementation", libs.findLibrary("hilt-testing").get())
            // Allows KSP to generate Hilt components for your tests
            add("kspAndroidTest", libs.findLibrary("hilt-compiler").get())

            // Unit Tests (Optional, for Robolectric, etc.)
            add("testImplementation", libs.findLibrary("hilt-testing").get())
            add("kspTest", libs.findLibrary("hilt-compiler").get())
        }
    }
}