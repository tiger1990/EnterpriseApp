package com.enterprise.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import java.net.URLEncoder

/**
 * Type-safe route hierarchy for Navigation 3.
 *
 * Rules:
 *  - Every route is a @Serializable data class / object.
 *  - AppRoute extends NavKey — the Nav3 marker interface required for back stack entries.
 *  - Routes live in :core:navigation so every module can depend on them
 *    WITHOUT creating circular dependencies.
 *  - No business logic here — pure data containers for navigation arguments.
 *  - Deep-link URI patterns are defined as constants on each route's companion.
 *    NOTE: Nav3 alpha does not support deep links natively; see MainActivity for handling.
 */
sealed interface AppRoute : NavKey

// ─── Top-level destinations ───────────────────────────────────────────────────

@Serializable
data object HomeRoute : AppRoute {
    const val DEEP_LINK_URI = "enterprise://home"
}

@Serializable
data class DetailRoute(
    val itemId: String = "",
    val itemTitle: String = ""
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

/** Fallback route for unrecognised deep links or navigation errors. */
@Serializable
data object ErrorRoute : AppRoute

// ─── Nested destinations (within feature graphs) ──────────────────────────────

@Serializable
data class EditProfileRoute(
    val userId: String = "",
) : AppRoute {
    companion object {
        const val DEEP_LINK_URI = "enterprise://profile/edit/{userId}"
    }
}

// ─── Serialization ────────────────────────────────────────────────────────────

/**
 * Explicit polymorphic serialization module for AppRoute.
 * Prevents reflection-based slowdowns and R8/ProGuard issues.
 */
val appSerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(HomeRoute::class)
        subclass(DetailRoute::class)
        subclass(SearchRoute::class)
        subclass(ProfileRoute::class)
        subclass(SettingsRoute::class)
        subclass(EditProfileRoute::class)
        subclass(ErrorRoute::class)
    }
}

// ─── Utility ──────────────────────────────────────────────────────────────────

private fun String.encodeForDeepLink(): String = URLEncoder.encode(this, "UTF-8")
