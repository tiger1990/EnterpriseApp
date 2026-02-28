pluginManagement {
    includeBuild("build-logic")          // Convention plugins live here
    repositories {
        google { mavenContent { includeGroupByRegex("com\\.android.*"); includeGroupByRegex("com\\.google.*"); includeGroupByRegex("androidx.*") } }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google { mavenContent { includeGroupByRegex("com\\.android.*"); includeGroupByRegex("com\\.google.*"); includeGroupByRegex("androidx.*") } }
        mavenCentral()
    }
}

rootProject.name = "EnterpriseNav3App"

// ── Core ──────────────────────────────────────────────────────────────────────
include(":core:common")
include(":core:data")
include(":core:domain")
include(":core:navigation")
include(":core:ui")
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