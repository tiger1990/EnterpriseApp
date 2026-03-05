package convention

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType

@Suppress("unused")
class AndroidKspConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Apply the KSP plugin itself
            pluginManager.apply("com.google.devtools.ksp")

            // Register the generated sources for all variants
            val androidComponents = extensions.findByType<AndroidComponentsExtension<*, *, *>>()
            androidComponents?.onVariants { variant ->
                // KSP usually registers its generated sources automatically.
                // If you encounter issues with "built-in Kotlin" in AGP 9.x, 
                // you might need to remove manual registration or use android.sourceSets.
                val kspGeneratedDir = layout.buildDirectory.dir("generated/ksp/${variant.name}/kotlin")
                variant.sources.kotlin?.addStaticSourceDirectory(kspGeneratedDir.get().asFile.absolutePath)
            }
        }
    }
}
