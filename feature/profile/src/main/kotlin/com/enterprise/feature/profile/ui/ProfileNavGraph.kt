package com.enterprise.feature.profile.ui

import com.enterprise.core.navigation.EditProfileRoute
import com.enterprise.core.navigation.EntryBuilder
import com.enterprise.core.navigation.ProfileRoute

/**
 * Registers Profile and EditProfile destinations.
 *
 * Deep links (enterprise://profile, enterprise://profile/edit/{userId}) —
 * see ProfileRoute.DEEP_LINK_URI and EditProfileRoute.DEEP_LINK_URI.
 * Handled in DeepLinkRouter / MainActivity.
 */
fun EntryBuilder.profileEntries() {
    scope.entry<ProfileRoute> {
        ProfileScreen()
    }

    scope.entry<EditProfileRoute> { route ->
        // EditProfileScreen(userId = route.userId) — replace with real screen
        ProfileScreen()
    }
}
