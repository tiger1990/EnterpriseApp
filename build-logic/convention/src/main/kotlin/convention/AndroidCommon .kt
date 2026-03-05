package convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// 1. Define a shared function for the properties STILL in CommonExtension
internal fun CommonExtension.configureSharedAndroid() {
    compileSdk = 36
    buildFeatures.buildConfig = true
}

/**
 * Shared configuration that still exists in CommonExtension (AGP 9.0+)
 */
internal fun Project.configureAndroidBase(commonExtension: CommonExtension) {
    commonExtension.apply {
        configureSharedAndroid()

        tasks.withType<KotlinCompile>().configureEach {
            // compilerOptions: This is the type-safe DSL introduced to replace the old kotlinOptions.
            compilerOptions { // Replaces kotlinOptions
                // Instead of passing a string like "17", you now use the JvmTarget enum for better compile-time safety.
                // jvmTarget.set(): The new API uses Gradle Properties, so you use .set() instead of =.
                jvmTarget.set(JvmTarget.JVM_17) // Use the JvmTarget enum instead of a string
                // languageVersion.set(KotlinVersion.KOTLIN_2_1)
                // freeCompilerArgs is now a ListProperty,
                // you use addAll() to append your flags rather than re-assigning the whole list.
                freeCompilerArgs.addAll(
                    "-opt-in=kotlin.RequiresOptIn",
                    "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                    "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
                    "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                )
            }
        }
    }
}


// 2. Specific function for Application modules
internal fun Project.configureAppModule(extension: ApplicationExtension) {
    extension.apply {
        configureAndroidBase(extension)
        defaultConfig {
            minSdk = 28
            testInstrumentationRunner = "com.enterprise.app.testing.HiltTestRunner"
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
        testOptions{
            unitTests.isIncludeAndroidResources = true
            unitTests.isReturnDefaultValues = true
        }
        buildFeatures{
            buildConfig = true
        }
    }
}

// 3. Specific function for Library modules
internal fun Project.configureLibraryModule(extension: LibraryExtension) {
    extension.apply {
        configureAndroidBase(extension)
        defaultConfig {
            minSdk = 28
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
        testOptions{
            unitTests.isIncludeAndroidResources = true
            unitTests.isReturnDefaultValues = true
        }
        buildFeatures{
            buildConfig = true
        }
    }
}

/**
 * Shared Android configuration applied to every library and application module.
 * Single place to control compileSdk, minSdk, JVM target, and Kotlin options.
 * Alternative: Safe Casting (If you want one function)
 * If you prefer keeping one function, you must cast the extension to access the missing properties:
 * // Use 'when' or safe casting to access specific properties
 * //        when (this) {
 * //            is ApplicationExtension -> {
 * //            }
 * //            is LibraryExtension -> {
 * //            }
 * //        }
 */

//internal fun Project.configureAndroidCommon(commonExtension: CommonExtension) {
//    commonExtension.apply {
//        compileSdk = 36
//
//        defaultConfig.minSdk = 26
//        defaultConfig.testInstrumentationRunner = "com.enterprise.app.testing.HiltTestRunner"
//
//        compileOptions.sourceCompatibility = JavaVersion.VERSION_17
//        compileOptions.targetCompatibility = JavaVersion.VERSION_17
//
//        buildFeatures.buildConfig = true
//
//        testOptions.unitTests.isIncludeAndroidResources = true
//        testOptions.unitTests.isReturnDefaultValues = true
//    }
//}