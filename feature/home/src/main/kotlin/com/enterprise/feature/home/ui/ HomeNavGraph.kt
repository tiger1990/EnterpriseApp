package com.enterprise.feature.home.ui

import androidx.navigation.compose.composable
import com.enterprise.core.navigation.HomeRoute
import com.enterprise.core.navigation.NavGraphBuilderScope

/**
 * Registers the Home feature's destinations.
 *
 * Called from :app's AppNavHost setup.
 * :app depends on :feature:home but feature modules do NOT depend on :app.
 *
 * Deep link: enterprise://home
 */
fun NavGraphBuilderScope.homeGraph() {
    builder.composable<HomeRoute>(
        deepLinks = listOf(
            androidx.navigation.navDeepLink { uriPattern = HomeRoute.DEEP_LINK_URI }
        ),
    ) {
        HomeScreen()
    }
}