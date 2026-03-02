package com.enterprise.feature.detail.ui

import androidx.navigation.compose.composable
import com.enterprise.core.navigation.HomeRoute
import com.enterprise.core.navigation.NavGraphBuilderScope


// ─── NavGraph extension ───────────────────────────────────────────────────────

fun NavGraphBuilderScope.detailGraph() {
    builder.composable<DetailRoute>(
        deepLinks = listOf(
            androidx.navigation.navDeepLink {
                uriPattern = DetailRoute.DEEP_LINK_URI
            }
        ),
    ) {
        // Nav3 automatically populates SavedStateHandle from type-safe route args
        DetailScreen()
    }
}
