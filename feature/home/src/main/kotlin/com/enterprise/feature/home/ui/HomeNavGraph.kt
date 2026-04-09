package com.enterprise.feature.home.ui

import com.enterprise.core.navigation.EntryBuilder
import com.enterprise.core.navigation.HomeRoute

/**
 * Registers the Home feature's destination.
 *
 * Deep link (enterprise://home) — see HomeRoute.DEEP_LINK_URI.
 * Handled in DeepLinkRouter / MainActivity.
 */
fun EntryBuilder.homeEntries() {
    scope.entry<HomeRoute> {
        HomeScreen()
    }
}
