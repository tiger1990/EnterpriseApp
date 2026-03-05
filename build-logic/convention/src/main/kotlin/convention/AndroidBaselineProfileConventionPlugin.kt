package convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

@Suppress("unused")
// ─── Baseline Profile ─────────────────────────────────────────────────────────
class AndroidBaselineProfileConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
        pluginManager.apply("androidx.baselineprofile")
        dependencies {
            add("implementation", libs.findLibrary("androidx.profileinstaller").get())
        }

        // Producer Specific Config (Managed Devices)
//        extensions.configure<BaselineProfileProducerExtension> {
//            // Using GMD (Gradle Managed Devices)
//            managedDevices.add("pixel7Api36")
//
//            // Note: 'filter' is for selecting specific classes to include/exclude
//            // e.g., filter { include("com.enterprise.**") }
//            filter {
//                // If 'warnings' is still not found, it is because
//                // the plugin version you're using expects this:
//            }
//        }
    }
}
