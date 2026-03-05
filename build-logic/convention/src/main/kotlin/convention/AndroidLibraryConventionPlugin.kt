package convention

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

@Suppress("unused")
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