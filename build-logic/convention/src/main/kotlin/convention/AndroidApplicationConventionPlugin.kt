package convention

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

@Suppress("unused")
// ─── Android Application ─────────────────────────────────────────────────────
class AndroidApplicationConventionPlugin : Plugin<Project> {
//refer later : https://medium.com/@amsavarthan/unlocking-reusability-in-gradle-how-to-use-kotlin-written-convention-plugins-11b95cb008ef
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("com.android.application")
        }
        extensions.configure<ApplicationExtension> {
            configureAppModule(this)
            defaultConfig.targetSdk = 36
        }
    }
}