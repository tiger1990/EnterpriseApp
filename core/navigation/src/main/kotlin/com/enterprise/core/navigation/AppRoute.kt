package com.enterprise.core.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe route hierarchy for Navigation 3.
 *
 * Rules:
 *  - Every route is a @Serializable data class / object.
 *  - Routes live in :core:navigation so every module can depend on them
 *    WITHOUT creating circular dependencies.
 *  - No business logic here — pure data containers for navigation arguments.
 *  - Deep-link URI patterns are defined as constants on each route's companion.
 */
sealed interface AppRoute

// ─── Top-level destinations ───────────────────────────────────────────────────

@Serializable
data object HomeRoute : AppRoute {
    const val DEEP_LINK_URI = "enterprise://home"
}

@Serializable
data class DetailRoute(
    val itemId: String,
    val itemTitle: String,
) : AppRoute {
    companion object {
        const val DEEP_LINK_URI = "enterprise://detail/{itemId}/{itemTitle}"
        fun deepLink(itemId: String, itemTitle: String) =
            "enterprise://detail/$itemId/${itemTitle.encodeForDeepLink()}"
    }
}

@Serializable
data object SearchRoute : AppRoute {
    const val DEEP_LINK_URI = "enterprise://search"
}

@Serializable
data object ProfileRoute : AppRoute {
    const val DEEP_LINK_URI = "enterprise://profile"
}

@Serializable
data object SettingsRoute : AppRoute {
    const val DEEP_LINK_URI = "enterprise://settings"
}

// ─── Nested destinations (within feature graphs) ──────────────────────────────

@Serializable
data class EditProfileRoute(
    val userId: String,
) : AppRoute {
    companion object {
        const val DEEP_LINK_URI = "enterprise://profile/edit/{userId}"
    }
}

// ─── Utility ──────────────────────────────────────────────────────────────────

private fun String.encodeForDeepLink(): String =
    java.net.URLEncoder.encode(this, "UTF-8")