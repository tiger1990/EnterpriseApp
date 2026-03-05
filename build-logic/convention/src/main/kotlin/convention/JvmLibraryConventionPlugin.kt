package convention

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
// ─── JVM Library (domain / use-case modules with no Android deps) ─────────────
class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("org.jetbrains.kotlin.jvm")
        }
        // No Android extension; pure Kotlin
    }
}