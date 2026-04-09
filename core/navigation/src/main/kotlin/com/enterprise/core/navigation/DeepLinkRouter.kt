package com.enterprise.core.navigation

import android.net.Uri

/**
 * Single source of truth for URI → AppRoute resolution.
 *
 * ─── Why this exists ─────────────────────────────────────────────────────────
 *
 * Deep link URI patterns are already defined as constants on each route's
 * companion object (e.g. HomeRoute.DEEP_LINK_URI = "enterprise://home").
 * This object is the only place that maps incoming URIs back to those routes.
 *
 * Keeping it in :core:navigation means:
 *   - Route knowledge never leaks into :app or feature modules
 *   - Adding or renaming a route requires changes in exactly one place
 *   - AppNavHost, MainActivity, and EnterpriseApp stay route-agnostic
 *
 * ─── Supported URI patterns ───────────────────────────────────────────────────
 *
 *   enterprise://home                        → HomeRoute
 *   enterprise://search                      → SearchRoute
 *   enterprise://settings                    → SettingsRoute
 *   enterprise://profile                     → ProfileRoute
 *   enterprise://profile/edit/{userId}       → EditProfileRoute
 *   enterprise://detail/{itemId}/{itemTitle} → DetailRoute
 *   https://www.travelmonk.com/**            → HomeRoute (web fallback)
 *   https://travelmonk.com/**               → HomeRoute (web fallback, no-www variant)
 *
 * Returns ErrorRoute for malformed enterprise URIs to provide user feedback.
*/*/
 */
object DeepLinkRouter {

    private val TRAVELMONK_HOSTS = setOf("www.travelmonk.com", "travelmonk.com")
    private val WEB_SCHEMES = setOf("http", "https")

    fun resolve(uri: Uri): AppRoute? = when {
        uri.scheme == "enterprise" -> resolveEnterpriseScheme(uri)
        uri.host in TRAVELMONK_HOSTS && uri.scheme in WEB_SCHEMES -> HomeRoute
        else -> null
    }

    private fun resolveEnterpriseScheme(uri: Uri): AppRoute = when (uri.host) {
        "home" -> HomeRoute
        "search" -> SearchRoute
        "settings" -> SettingsRoute
        "profile" -> resolveProfileScheme(uri)
        "detail" -> resolveDetailScheme(uri)
        else -> ErrorRoute
    }

    private fun resolveProfileScheme(uri: Uri): AppRoute =
        if (uri.pathSegments.firstOrNull() == "edit") {
            uri.pathSegments.getOrNull(1)?.let { EditProfileRoute(userId = it) } ?: ErrorRoute
        } else {
            ProfileRoute
        }

    // Android's Uri.pathSegments already URL-decodes each segment — no manual decode needed.
    private fun resolveDetailScheme(uri: Uri): AppRoute {
        val itemId = uri.pathSegments.getOrNull(0) ?: return ErrorRoute
        val itemTitle = uri.pathSegments.getOrNull(1) ?: return ErrorRoute
        return DetailRoute(itemId = itemId, itemTitle = itemTitle)
    }
}
