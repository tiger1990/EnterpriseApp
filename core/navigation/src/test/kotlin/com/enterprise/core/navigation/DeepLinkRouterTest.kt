package com.enterprise.core.navigation

import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Unit tests for DeepLinkRouter.
 *
 * Uses Robolectric to provide android.net.Uri on the JVM.
 * DeepLinkRouter is a pure function — no coroutines, no DI.
 *
 * Coverage:
 *   - All supported enterprise:// routes (happy paths)
 *   - enterprise:// edge cases: missing segments, unknown host
 *   - travelmonk.com: www + no-www, http + https
 *   - Unknown schemes → null (no navigation)
 *   - URL-encoded characters in path segments
 */
@RunWith(RobolectricTestRunner::class)
class DeepLinkRouterTest {

    // ── enterprise:// happy paths ─────────────────────────────────────────────

    @Test fun `home route resolves`() {
        assertEquals(HomeRoute, resolve("enterprise://home"))
    }

    @Test fun `search route resolves`() {
        assertEquals(SearchRoute, resolve("enterprise://search"))
    }

    @Test fun `settings route resolves`() {
        assertEquals(SettingsRoute, resolve("enterprise://settings"))
    }

    @Test fun `profile route resolves`() {
        assertEquals(ProfileRoute, resolve("enterprise://profile"))
    }

    @Test fun `editProfile route resolves with userId`() {
        assertEquals(
            EditProfileRoute(userId = "user123"),
            resolve("enterprise://profile/edit/user123"),
        )
    }

    @Test fun `detail route resolves with itemId and decoded itemTitle`() {
        // Uri.pathSegments URL-decodes each segment automatically
        val result = resolve("enterprise://detail/42/Hello%20World") as DetailRoute
        assertEquals("42", result.itemId)
        assertEquals("Hello World", result.itemTitle)
    }

    @Test fun `detail route resolves with plus-encoded title`() {
        val result = resolve("enterprise://detail/99/My+Item") as DetailRoute
        assertEquals("99", result.itemId)
        // Uri.pathSegments does not decode '+' as space — it is treated literally
        assertEquals("My+Item", result.itemTitle)
    }

    // ── enterprise:// edge cases ──────────────────────────────────────────────

    @Test fun `unknown enterprise host returns ErrorRoute`() {
        assertEquals(ErrorRoute, resolve("enterprise://unknown-host"))
    }

    @Test fun `detail with missing itemTitle returns ErrorRoute`() {
        assertEquals(ErrorRoute, resolve("enterprise://detail/42"))
    }

    @Test fun `detail with no path segments returns ErrorRoute`() {
        assertEquals(ErrorRoute, resolve("enterprise://detail"))
    }

    @Test fun `editProfile with missing userId returns ErrorRoute`() {
        assertEquals(ErrorRoute, resolve("enterprise://profile/edit"))
    }

    @Test fun `profile with unrecognised sub-path returns ProfileRoute`() {
        // "profile/other" — firstOrNull() != "edit" → falls through to ProfileRoute
        assertEquals(ProfileRoute, resolve("enterprise://profile/other"))
    }

    // ── travelmonk.com — www variant ──────────────────────────────────────────

    @Test fun `https www travelmonk resolves to HomeRoute`() {
        assertEquals(HomeRoute, resolve("https://www.travelmonk.com/anything"))
    }

    @Test fun `http www travelmonk resolves to HomeRoute`() {
        assertEquals(HomeRoute, resolve("http://www.travelmonk.com/"))
    }

    // ── travelmonk.com — no-www variant ───────────────────────────────────────

    @Test fun `https bare travelmonk resolves to HomeRoute`() {
        assertEquals(HomeRoute, resolve("https://travelmonk.com/some/path"))
    }

    @Test fun `http bare travelmonk resolves to HomeRoute`() {
        assertEquals(HomeRoute, resolve("http://travelmonk.com"))
    }

    // ── unknown scheme / host — must return null ───────────────────────────────

    @Test fun `unknown host returns null`() {
        assertNull(resolve("https://someother.example.com"))
    }

    @Test fun `mailto scheme returns null`() {
        assertNull(resolve("mailto:user@example.com"))
    }

    @Test fun `ftp travelmonk returns null due to scheme guard`() {
        assertNull(resolve("ftp://www.travelmonk.com"))
    }

    @Test fun `non-travelmonk https returns null`() {
        assertNull(resolve("https://google.com"))
    }

    // ── helper ────────────────────────────────────────────────────────────────

    private fun resolve(uriString: String): AppRoute? =
        DeepLinkRouter.resolve(Uri.parse(uriString))
}
