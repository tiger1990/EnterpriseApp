package com.enterprise.core.navigation

import kotlinx.coroutines.flow.SharedFlow

/**
 * NavigationMiddleware is the ONLY mechanism through which a ViewModel
 * can request navigation changes.
 *
 * ─── Why this exists ─────────────────────────────────────────────────────────
 *
 * ❌ Anti-pattern: ViewModel holds NavController → untestable, leaks Activity
 * ❌ Anti-pattern: Singleton navigator → lifecycle-unaware, process-death issues
 * ❌ Anti-pattern: UI calls navigator directly → bypasses MVI reducer
 *
 * ✅ This pattern:
 *   1. ViewModel emits a NavigationEvent (a sealed class — pure data)
 *   2. NavigationMiddleware collects events from the ViewModel's SharedFlow
 *   3. The single AppNavHost (in :app) subscribes and performs the actual
 *      NavController operation inside a LaunchedEffect — scoped to the
 *      Composition lifecycle.
 *
 * The NavController never leaves the AppNavHost composable.
 */
interface NavigationMiddleware {
    /** Hot flow of pending navigation commands. Collect in AppNavHost. */
    val navigationEvents: SharedFlow<NavigationEvent>
}

/**
 * Sealed hierarchy of navigation commands.
 * All commands are pure data — no NavController references.
 */
sealed interface NavigationEvent {

    /** Navigate to a route. Back-stack behaviour controlled by [options]. */
    data class NavigateTo(
        val route: AppRoute,
        val options: NavOptions = NavOptions(),
    ) : NavigationEvent

    /** Navigate up (same as pressing the system Back button). */
    data object NavigateUp : NavigationEvent

    /** Pop the back stack to a specific destination. */
    data class PopUpTo(
        val route: AppRoute,
        val inclusive: Boolean = false,
        val saveState: Boolean = false,
    ) : NavigationEvent

    /** Pop everything and navigate to root (used for deep links / tab switches). */
    data class PopToRoot(
        val root: AppRoute,
    ) : NavigationEvent
}

/**
 * Options mirroring NavOptionsBuilder without importing navigation at call site.
 */
data class NavOptions(
    val singleTop: Boolean = true,
    val restoreState: Boolean = false,
    val popUpToRoute: AppRoute? = null,
    val popUpToInclusive: Boolean = false,
    val popUpToSaveState: Boolean = false,
)