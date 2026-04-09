package com.enterprise.feature.settings.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.navigation3.ui.NavDisplay
import com.enterprise.core.navigation.EntryBuilder
import com.enterprise.core.navigation.SettingsRoute

/**
 * Registers the Settings feature's destination.
 *
 * Transition: fade-only (no directional slide — settings is not a drill-down).
 * Declared via Nav3's metadata map — AppNavHost is not modified.
 *
 * Deep link (enterprise://settings) — see SettingsRoute.DEEP_LINK_URI.
 * Handled in DeepLinkRouter / MainActivity.
 */
fun EntryBuilder.settingsEntries() {
    scope.entry<SettingsRoute>(
        metadata = NavDisplay.transitionSpec {
            fadeIn(tween(350)) togetherWith fadeOut(tween(350))
        } + NavDisplay.popTransitionSpec {
            fadeIn(tween(350)) togetherWith fadeOut(tween(350))
        },
    ) {
        SettingsScreen()
    }
}
