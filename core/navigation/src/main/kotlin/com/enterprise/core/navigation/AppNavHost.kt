package com.enterprise.core.navigation

import android.net.Uri
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.coroutines.flow.Flow

/**
 * AppNavHost — single composition-scoped owner of the Nav3 back stack.
 *
 * Architecture contract (unchanged from Nav2):
 *  - Back stack is created here via rememberNavBackStack() and NEVER
 *    passed down to children or stored in any ViewModel.
 *  - ViewModels emit NavigationEvents → NavigationEventBus → collected here.
 *  - Each feature registers its composable destinations via EntryBuilder
 *    extensions (see feature modules).
 *
 * Nav3 changes vs Nav2:
 *  - rememberNavController()  →  rememberNavBackStack()
 *  - NavHost { ... }          →  NavDisplay(entryProvider = entryProvider { ... })
 *  - NavGraphBuilder.composable<R> { }  →  EntryProviderScope.entry<R> { }
 *  - NavController.navigate() →  backStack.add() / removeLastOrNull() / etc.
 *
 * Deep links:
 *  - [deepLinkUri] is passed down from MainActivity (Compose-observable state).
 *  - DeepLinkRouter resolves URI → AppRoute; resolution lives entirely in
 *    :core:navigation so neither MainActivity nor feature modules know about
 *    URI patterns.
 *
 * @param navigationEvents  Hot flow from NavigationEventBus (injected to Activity).
 * @param deepLinkUri       URI from an incoming Intent (null if no deep link).
 * @param entriesBuilder    Lambda that registers all feature entry destinations.
 */
@Composable
fun AppNavHost(
    navigationEvents: Flow<NavigationEvent>,
    modifier: Modifier = Modifier,
    deepLinkUri: Uri? = null,
    startDestination: AppRoute = HomeRoute,
    entriesBuilder: EntryBuilder.() -> Unit,
) {
    // ── Explicit Serialization Configuration ──────────────────────────────────
    // appSerializersModule (AppRoute.kt) prevents reflection fallback and ProGuard stripping.
    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration { serializersModule = appSerializersModule },
        startDestination
    )

    // remember: same lambda reference across recompositions so NavDisplay
    // doesn't treat it as a changed param on every back-stack mutation.
    val onBack: () -> Unit = remember(backStack) { { backStack.removeLastOrNull() } }

    // remember: entryProvider is built once per entriesBuilder identity.
    // Recreating it on every recomposition would re-register all destinations
    // and allocate new lambdas for each entry unnecessarily.
    val provider = remember(backStack, entriesBuilder) {
        entryProvider {
            entry<ErrorRoute> {
                ErrorScreen(
                    // onGoHome captures backStack — same reference, so safe to remember
                    onGoHome = {
                        backStack.handleNavigationEvent(NavigationEvent.PopToRoot(HomeRoute))
                    },
                )
            }
            EntryBuilder(this).entriesBuilder()
        }
    }

    // ── Collect navigation events emitted by ViewModels ──────────────────────
    LaunchedEffect(backStack) {
        navigationEvents.collect { event ->
            backStack.handleNavigationEvent(event)
        }
    }

    // ── Deep link handling ────────────────────────────────────────────────────
    // Runs after first composition (NavDisplay collector is already active).
    // Re-runs only when deepLinkUri changes (launch-time or onNewIntent).
    // URI → AppRoute resolution stays in DeepLinkRouter — no route knowledge here.
    LaunchedEffect(deepLinkUri) {
        val route = deepLinkUri?.let { DeepLinkRouter.resolve(it) } ?: return@LaunchedEffect
        backStack.handleNavigationEvent(
            NavigationEvent.NavigateTo(
                route   = route,
                options = NavOptions(singleTop = true, popUpToRoute = HomeRoute, popUpToInclusive = false),
            )
        )
    }

    NavDisplay(
        backStack       = backStack,
        onBack          = onBack,
        modifier        = modifier,
        entryProvider   = provider,
        // Top-level constants — no allocation on every transition or recomposition
        transitionSpec    = { NavForwardTransition },
        popTransitionSpec = { NavBackTransition },
    )
}

// ─── Navigation event handler ─────────────────────────────────────────────────
//
// The back stack is a SnapshotStateList<NavKey>, so mutations trigger recomposition
// automatically. All Nav2 NavController semantics are replicated here.

private fun MutableList<NavKey>.handleNavigationEvent(event: NavigationEvent) {
    when (event) {
        is NavigationEvent.NavigateTo -> {
            // singleTop: skip push if the route class is already at the top
            if (event.options.singleTop &&
                isNotEmpty() &&
                last() == event.route
            ) return

            event.options.popUpToRoute?.let { popRoute ->
                popTo(popRoute, event.options.popUpToInclusive)
            }
            add(event.route)
        }

        is NavigationEvent.NavigateUp -> removeLastOrNull()

        is NavigationEvent.PopUpTo -> popTo(event.route, event.inclusive)

        is NavigationEvent.PopToRoot -> {
            // Keep the root entry; clear everything above it.
            val rootIndex = indexOfFirst { it::class == event.root::class }
            if (rootIndex >= 0) {
                subList(rootIndex + 1, size).clear()
            } else {
                // Root not in stack — clear and push it fresh.
                clear()
                add(event.root)
            }
        }
    }
}

/**
 * Pop the back stack to the last entry matching [route]'s class.
 * If [inclusive] is true the matching entry itself is also removed.
 */
private fun MutableList<NavKey>.popTo(route: AppRoute, inclusive: Boolean) {
    val index = indexOfLast { it::class == route::class }
    if (index >= 0) {
        val removeFrom = if (inclusive) index else index + 1
        if (removeFrom < size) subList(removeFrom, size).clear()
    }
}

// ─── Error screen ─────────────────────────────────────────────────────────────
//
// Shown whenever DeepLinkRouter resolves an enterprise:// URI it cannot match.
// Kept in core:navigation alongside ErrorRoute — no ViewModel needed since
// AppNavHost owns the backStack and passes the callback directly.

@Composable
private fun ErrorScreen(onGoHome: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Link not found",
                style = MaterialTheme.typography.headlineSmall,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "The link you followed is invalid or has expired.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onGoHome) {
                Text("Go to Home")
            }
        }
    }
}

/**
 * Thin wrapper around Nav3's EntryProviderScope.
 *
 * Feature modules extend this via extension functions, calling scope.entry<T> { }
 * from Nav3's API to register composable destinations.
 *
 * The wrapper keeps EntryProviderScope<NavKey> as an implementation detail —
 * feature modules only import EntryBuilder from core:navigation.
 *
 * Example (in feature:home):
 *   fun EntryBuilder.homeEntries() {
 *       scope.entry<HomeRoute> { HomeScreen() }
 *   }
 */
// ─── Transition specs ─────────────────────────────────────────────────────────
//
// Top-level vals: created once, never recreated on recomposition.
// Passing inline lambdas to NavDisplay would create new objects every time
// AppNavHost recomposes (e.g. on every back-stack change).

private val NavForwardTransition =
    slideInHorizontally(tween(300)) { it / 4 } + fadeIn(tween(300)) togetherWith
        slideOutHorizontally(tween(300)) { -it / 4 } + fadeOut(tween(300))

private val NavBackTransition =
    slideInHorizontally(tween(300)) { -it / 4 } + fadeIn(tween(300)) togetherWith
        slideOutHorizontally(tween(300)) { it / 4 } + fadeOut(tween(300))

// ─── EntryBuilder wrapper ─────────────────────────────────────────────────────

@JvmInline
value class EntryBuilder(val scope: EntryProviderScope<NavKey>)
