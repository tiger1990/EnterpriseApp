package com.enterprise.feature.profile.ui

import androidx.navigation.compose.composable
import com.enterprise.core.navigation.ProfileRoute
import com.enterprise.core.navigation.NavGraphBuilderScope

// ═══════════════════════════ NavGraph ═════════════════════════════════════════

fun NavGraphBuilderScope.profileGraph() {
    builder.composable<ProfileRoute>(
        deepLinks = listOf(
            androidx.navigation.navDeepLink { uriPattern = ProfileRoute.DEEP_LINK_URI }
        ),
    ) {
        ProfileScreen()
    }

    builder.composable<EditProfileRoute>(
        deepLinks = listOf(
            androidx.navigation.navDeepLink { uriPattern = EditProfileRoute.DEEP_LINK_URI }
        ),
    ) {
        // EditProfileScreen would go here — omitted for brevity
        ProfileScreen()
    }
}
