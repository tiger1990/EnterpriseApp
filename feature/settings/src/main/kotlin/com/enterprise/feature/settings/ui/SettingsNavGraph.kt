package com.enterprise.feature.settings.ui

import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.enterprise.core.navigation.SettingsRoute
import com.enterprise.core.navigation.NavGraphBuilderScope

// ═══════════════════════════ NavGraph ═════════════════════════════════════════

fun NavGraphBuilderScope.settingsGraph() {
    builder.composable<SettingsRoute>(
        deepLinks = listOf(
            navDeepLink { uriPattern = SettingsRoute.DEEP_LINK_URI }
        ),
    ) {
        SettingsScreen()
    }
}