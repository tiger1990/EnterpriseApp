# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK (requires KSTOREPWD and KEYPWD env vars)
./gradlew installDebug           # Install debug APK on connected device
./gradlew test                   # Run all unit tests
./gradlew :feature:home:test     # Run unit tests for a single module
./gradlew androidTest            # Run instrumented tests (requires connected device/emulator)
./gradlew lint                   # Run Android lint
./gradlew generateBaselineProfile # Generate startup baseline profiles
```

## Architecture Overview

### Module Structure

```
app/               → App shell only: Activity, top-level nav graph, app-level DI
core/              → Framework-agnostic, reusable layers
  ├── common/      → Base MVI ViewModel, Result type, shared utilities
  ├── data/        → Repository implementations, data sources
  ├── domain/      → Use cases and models (ZERO framework dependencies)
  ├── navigation/  → AppRoute definitions, AppNavHost, NavigationEventBus
  ├── network/     → Retrofit/OkHttp setup
  ├── tokens/      → Design tokens (colors, typography)
  ├── ui/          → Shared Compose components, Material3 theming
  └── testing/     → HiltTestRunner, shared test utilities
feature/           → Self-contained feature modules (home, detail, search, profile, settings)
build-logic/       → Convention plugins as Gradle DSL (see convention plugins section)
```

**Dependency rule:** Features only import `core:*` modules. Features never depend on other features. `:app` is the only module that imports all features.

### MVI Pattern

Every feature follows strict MVI. Each feature's `mvi/` directory contains:
- **State** — immutable `data class` implementing `UiState`
- **Action** — `sealed interface` implementing `UiAction` (user/system intents)
- **Effect** — `sealed interface` implementing `UiEffect` (one-shot events like Snackbar)
- **Reducer** — pure function `(State, Action) -> State`
- **ViewModel** — extends `MviViewModel<State, Action, Effect>` from `core:common`

ViewModels call `navigate(NavigationEvent)` rather than holding a `NavController`. Effects handle one-shot UI events; navigation goes through `NavigationEventBus`.

### Navigation

Routes are defined in `core/navigation/AppRoute.kt` as a sealed interface annotated with `@Serializable`. Each route companion object includes a `DEEP_LINK_URI` constant.

The **NavigationEventBus** is `@ActivityRetainedScoped` (survives config changes). ViewModels emit navigation events; `AppNavHost` in `:app` collects them and calls the `NavController`. ViewModels never hold a reference to `NavController`.

Feature nav graphs are **extension functions** on `NavGraphBuilderScope`, registered in `AppGraph.kt` inside `:app`.

Deep link URI scheme: `enterprise://` for internal routes; `http(s)://www.travelmonk.com` for web routes.

### Convention Plugins

Applied via `enterprise.*` aliases in module `build.gradle.kts` files:

| Plugin | Used for |
|--------|----------|
| `enterprise.android.application` | `:app` module base |
| `enterprise.android.application.compose` | `:app` + Compose |
| `enterprise.android.feature` | Feature modules (includes library, compose, hilt, navigation) |
| `enterprise.android.library` | Core library modules |
| `enterprise.android.library.compose` | Core library + Compose |
| `enterprise.android.hilt` | Hilt DI setup |
| `enterprise.android.ksp` | KSP annotation processing |
| `enterprise.jvm.library` | JVM-only modules (e.g., `core:domain`) |

### Dependency Injection

- `@HiltViewModel` for all feature ViewModels
- `@ActivityRetainedScoped` for `NavigationEventBus`
- `@SingletonComponent` for repositories
- Data module (`core/data/di/DataModule.kt`) binds domain interfaces to data implementations

### State Restoration (Process Death)

`SavedStateHandle` in ViewModels stores only lightweight values (IDs, tab indices, query strings). Full data is re-fetched from the repository on restore. NavController back-stack is automatically restored by the framework.

### Key Files

| File | Purpose |
|------|---------|
| `app/src/main/kotlin/.../ui/MainActivity.kt` | Activity shell; edge-to-edge enabled |
| `app/src/main/kotlin/.../navigation/AppGraph.kt` | Assembles all feature nav graphs |
| `core/navigation/src/.../AppRoute.kt` | All route definitions + deep link URIs |
| `core/navigation/src/.../AppNavHost.kt` | Single `NavController` owner |
| `core/navigation/src/.../NavigationEventBus.kt` | ViewModel → NavHost event bus |
| `core/common/src/.../mvi/MviViewModel.kt` | Base ViewModel all features extend |
| `gradle/libs.versions.toml` | Centralized dependency versions |

## Testing

- **Unit tests:** JUnit 4 + MockK + Turbine (for Flow assertions)
- **Instrumented tests:** Use `HiltTestRunner` from `core:testing`
- **Screenshot tests:** Paparazzi (no device required)
- **Android framework in unit tests:** Robolectric

Release signing uses `KSTOREPWD` and `KEYPWD` environment variables with keystore file `myreleasekey.keystore`.
