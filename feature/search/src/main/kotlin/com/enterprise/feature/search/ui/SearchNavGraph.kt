package com.enterprise.feature.search.ui

import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.enterprise.core.navigation.NavGraphBuilderScope
import com.enterprise.core.navigation.SearchRoute

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
            navDeepLink { uriPattern = SearchRoute.DEEP_LINK_URI }
        ),
    ) {
        SearchScreen()
    }
}