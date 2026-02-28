package com.enterprise.core.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.SharedFlow

/**
 * AppNavHost is the single composition-scoped owner of NavController.
 *
 * Architecture contract:
 *  - NavController is created here via rememberNavController() and NEVER
 *    passed down to children or stored in any ViewModel.
 *  - ViewModels emit NavigationEvents → NavigationEventBus → collected here.
 *  - Each feature registers its composable destinations via NavGraphBuilder
 *    extensions (see feature modules).
 *
 * @param navigationEvents  Hot flow from NavigationEventBus (injected to Activity).
 * @param graphBuilder      Lambda that adds all feature destinations.
 */
@Composable
fun AppNavHost(
    navigationEvents: SharedFlow<NavigationEvent>,
    modifier: Modifier = Modifier,
    startDestination: AppRoute = HomeRoute,
    graphBuilder: NavGraphBuilderScope.() -> Unit,
) {
    val navController: NavHostController = rememberNavController()

    // ── Collect navigation events emitted by ViewModels ──────────────────────
    // LaunchedEffect is tied to the Composition lifetime; cancels automatically.
    LaunchedEffect(navController) {
        navigationEvents.collect { event ->
            navController.handleNavigationEvent(event)
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition  = { defaultEnterTransition() },
        exitTransition   = { defaultExitTransition() },
        popEnterTransition  = { defaultPopEnterTransition() },
        popExitTransition   = { defaultPopExitTransition() },
    ) {
        NavGraphBuilderScope(this).graphBuilder()
    }
}

// ─── Navigation event handler ─────────────────────────────────────────────────

private fun NavController.handleNavigationEvent(event: NavigationEvent) {
    when (event) {
        is NavigationEvent.NavigateTo -> {
            navigate(route = event.route) {
                event.options.popUpToRoute?.let { popRoute ->
                    popUpTo(popRoute) {
                        inclusive = event.options.popUpToInclusive
                        saveState = event.options.popUpToSaveState
                    }
                }
                launchSingleTop = event.options.singleTop
                restoreState    = event.options.restoreState
            }
        }

        is NavigationEvent.NavigateUp -> navigateUp()

        is NavigationEvent.PopUpTo -> {
            popBackStack(route = event.route, inclusive = event.inclusive, saveState = event.saveState)
        }

        is NavigationEvent.PopToRoot -> {
            navigate(route = event.root) {
                popUpTo(graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState    = true
            }
        }
    }
}

// ─── Animated transition defaults ─────────────────────────────────────────────

private fun AnimatedContentTransitionScope<*>.defaultEnterTransition(): EnterTransition =
    slideInHorizontally(animationSpec = tween(300)) { it / 4 } + fadeIn(tween(300))

private fun AnimatedContentTransitionScope<*>.defaultExitTransition(): ExitTransition =
    slideOutHorizontally(animationSpec = tween(300)) { -it / 4 } + fadeOut(tween(300))

private fun AnimatedContentTransitionScope<*>.defaultPopEnterTransition(): EnterTransition =
    slideInHorizontally(animationSpec = tween(300)) { -it / 4 } + fadeIn(tween(300))

private fun AnimatedContentTransitionScope<*>.defaultPopExitTransition(): ExitTransition =
    slideOutHorizontally(animationSpec = tween(300)) { it / 4 } + fadeOut(tween(300))

// ─── Wrapper to allow features to add destinations without exposing NavGraphBuilder ──

/**
 * Thin wrapper around NavGraphBuilder. Features extend this via
 * extension functions to add their destinations.
 *
 * Example (in feature:home):
 *   fun NavGraphBuilderScope.homeGraph() {
 *       composable<HomeRoute> { HomeScreen() }
 *   }
 */
@JvmInline
value class NavGraphBuilderScope(val builder: androidx.navigation.NavGraphBuilder)