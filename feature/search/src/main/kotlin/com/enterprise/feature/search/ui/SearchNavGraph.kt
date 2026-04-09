package com.enterprise.feature.search.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.navigation3.ui.NavDisplay
import com.enterprise.core.navigation.EntryBuilder
import com.enterprise.core.navigation.SearchRoute

/**
 * Registers the Search feature's destination.
 *
 * Transition: slides up from the bottom (modal behavior).
 * Declared via Nav3's metadata map — AppNavHost is not modified.
 *
 * Deep link (enterprise://search) — see SearchRoute.DEEP_LINK_URI.
 * Handled in DeepLinkRouter / MainActivity.
 */
fun EntryBuilder.searchEntries() {
    scope.entry<SearchRoute>(
        metadata = NavDisplay.transitionSpec {
            (slideInVertically(tween(300)) { it } + fadeIn(tween(300))) togetherWith
                (slideOutVertically(tween(300)) { -it } + fadeOut(tween(300)))
        } + NavDisplay.popTransitionSpec {
            (slideInVertically(tween(300)) { -it } + fadeIn(tween(300))) togetherWith
                (slideOutVertically(tween(300)) { it } + fadeOut(tween(300)))
        },
    ) {
        SearchScreen()
    }
}
