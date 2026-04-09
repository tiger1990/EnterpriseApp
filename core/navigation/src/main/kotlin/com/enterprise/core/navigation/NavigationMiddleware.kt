package com.enterprise.core.navigation

import kotlinx.coroutines.flow.Flow

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
 *   2. NavigationMiddleware collects events from a Channel-backed Flow
 *   3. The single AppNavHost (in :app) subscribes and performs the actual
 *      back-stack operation inside a LaunchedEffect — scoped to the
 *      Composition lifecycle.
 *
 * The NavController never leaves the AppNavHost composable.
 *
 * ─── Why Flow and not SharedFlow ─────────────────────────────────────────────
 * SharedFlow(replay=1) re-delivers the last event to any new collector, which
 * causes double-navigation whenever AppNavHost's LaunchedEffect restarts (e.g.
 * after a configuration change that creates a new backStack instance).
 * A Channel-backed Flow gives exactly-once delivery: events are buffered until
 * consumed, and a restarted collector continues reading from the buffer without
 * seeing already-consumed events.
 */
interface NavigationMiddleware {
    /** Channel-backed flow of pending navigation commands. Collect in AppNavHost. */
    val navigationEvents: Flow<NavigationEvent>
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