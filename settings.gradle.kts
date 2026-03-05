pluginManagement {
    includeBuild("build-logic")          // Convention plugins live here
    repositories {
        google {
            mavenContent {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        // Uncomment and change the build ID if you need to use snapshot artifacts.
        // See androidx.dev for full instructions.
        /*maven {
            url = uri("https://androidx.dev/snapshots/builds/<build_id>/artifacts/repository")
        }*/
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google {
            mavenContent {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        // Uncomment and change the build ID if you need to use snapshot artifacts.
        // See androidx.dev for full instructions.
        /*maven {
            url = uri("https://androidx.dev/snapshots/builds/<build_id>/artifacts/repository")
        }*/

        // Add any other custom repositories here, e.g.:
        // maven { url "https://jitpack.io" }
    }
}

rootProject.name = "EnterpriseNav3App"

// ── Core ──────────────────────────────────────────────────────────────────────
include(":core:common")
include(":core:data")
include(":core:domain")
include(":core:navigation")
include(":core:ui")
include(":core:tokens")
include(":core:network")
include(":core:testing")

// ── Features ──────────────────────────────────────────────────────────────────
include(":feature:home")
include(":feature:detail")
include(":feature:profile")
include(":feature:search")
include(":feature:settings")

// ── App Shell ─────────────────────────────────────────────────────────────────
include(":app")

// ── Tooling ───────────────────────────────────────────────────────────────────
include(":baseline-profiles")