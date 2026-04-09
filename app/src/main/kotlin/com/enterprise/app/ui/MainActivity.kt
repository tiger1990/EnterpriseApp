package com.enterprise.app.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.enterprise.app.navigation.appEntries
import com.enterprise.app.theme.EnterpriseTheme
import com.enterprise.core.domain.repository.ThemeRepository
import com.enterprise.core.navigation.AppNavHost
import com.enterprise.core.navigation.NavigationEventBus
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * MainActivity — single Activity shell.
 *
 * Responsibilities:
 *   1. Edge-to-edge setup
 *   2. Theme wrapping
 *   3. Bridging Android's Intent system → Compose state (deep link URI)
 *   4. Providing NavigationEventBus to AppNavHost
 *
 * What it does NOT do:
 *   ❌ Know about any AppRoute
 *   ❌ Parse or interpret URIs
 *   ❌ Hold NavController / NavBackStack
 *   ❌ Make navigation decisions
 *
 * Deep link flow:
 *   Android Intent (URI)
 *     → pendingDeepLinkUri (Compose-observable state)
 *       → AppNavHost.deepLinkUri
 *         → DeepLinkRouter.resolve()   [all in core:navigation]
 *           → backStack.handleNavigationEvent()
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var navigationEventBus: NavigationEventBus
    @Inject lateinit var themeRepository: ThemeRepository

    /**
     * Compose-observable — writing from onNewIntent automatically
     * triggers recomposition and re-runs the deep link LaunchedEffect in AppNavHost.
     */
    private var pendingDeepLinkUri by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        pendingDeepLinkUri = intent.data

        setContent {
            val isDarkTheme by themeRepository.observeIsDarkTheme()
                .collectAsStateWithLifecycle(initialValue = false)
            EnterpriseTheme(darkTheme = isDarkTheme) {
                EnterpriseApp(
                    navigationEventBus = navigationEventBus,
                    deepLinkUri        = { pendingDeepLinkUri },
                )
            }
        }
    }

    /** Called when a new deep link arrives while the Activity is already running. */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        pendingDeepLinkUri = intent.data
    }
}

/**
 * Pure pass-through composable. No route knowledge. No navigation logic.
 * Exists only to keep MainActivity.setContent {} minimal.
 */
@Composable
private fun EnterpriseApp(
    navigationEventBus: NavigationEventBus,
    modifier: Modifier = Modifier,
    // Lambda defers the state read of pendingDeepLinkUri into the composition body.
    // android.net.Uri is not @Stable, so a direct Uri? param would make this
    // composable non-skippable. A () -> Uri? lambda is stable, allowing Compose
    // to skip EnterpriseApp on unrelated recompositions (e.g. isDarkTheme changes).
    deepLinkUri: () -> Uri? = { null },
) {
    // Stable reference: navigationEvents getter calls receiveAsFlow() on every access,
    // producing a new Flow object each time. Without remember, AppNavHost receives a
    // different Flow reference on every recomposition, preventing it from skipping.
    // remember(navigationEventBus) reuses the same instance for the bus's lifetime.
    val navigationEvents = remember(navigationEventBus) { navigationEventBus.navigationEvents }

    AppNavHost(
        navigationEvents = navigationEvents,
        deepLinkUri      = deepLinkUri(),   // unwrap lambda → Uri?; AppNavHost unchanged
        modifier         = modifier,
    ) {
        appEntries()
    }
}
