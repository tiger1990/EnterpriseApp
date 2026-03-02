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
// ═══════════════════════════ UI ═══════════════════════════════════════════════

fun NavGraphBuilderScope.searchGraph() {
    builder.composable<SearchRoute>(
        deepLinks = listOf(
            androidx.navigation.navDeepLink { uriPattern = SearchRoute.DEEP_LINK_URI }
        ),
    ) {
        SearchScreen()
    }
}