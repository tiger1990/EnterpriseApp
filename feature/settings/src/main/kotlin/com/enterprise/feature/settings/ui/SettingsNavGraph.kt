package com.enterprise.feature.home.ui

import androidx.navigation.compose.composable
import com.enterprise.core.navigation.SettingsRoute
import com.enterprise.core.navigation.NavGraphBuilderScope

// ═══════════════════════════ NavGraph ═════════════════════════════════════════

fun NavGraphBuilderScope.settingsGraph() {
    builder.composable<SettingsRoute>(
        deepLinks = listOf(
            androidx.navigation.navDeepLink { uriPattern = SettingsRoute.DEEP_LINK_URI }
        ),
    ) {
        SettingsScreen()
    }
}