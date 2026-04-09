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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.enterprise.app.navigation.appEntries
import com.enterprise.app.theme.EnterpriseTheme
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

    @Inject
    lateinit var navigationEventBus: NavigationEventBus

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
            EnterpriseTheme {
                EnterpriseApp(
                    navigationEventBus = navigationEventBus,
                    deepLinkUri        = pendingDeepLinkUri,
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
    deepLinkUri: Uri? = null,
) {
    AppNavHost(
        navigationEvents = navigationEventBus.navigationEvents,
        deepLinkUri      = deepLinkUri,
        modifier         = modifier,
    ) {
        appEntries()
    }
}
