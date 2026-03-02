package com.enterprise.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.enterprise.app.navigation.AppGraph
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
 *   3. Providing the NavigationEventBus to AppNavHost
 *
 * What it does NOT do:
 *   ❌ Hold NavController
 *   ❌ Make navigation decisions
 *   ❌ Know about feature destinations directly (delegated to AppGraph)
 *
 * Hilt scoping:
 *   NavigationEventBus is @ActivityRetainedScoped — injected here and shared
 *   with all ViewModels in this Activity via Hilt's activity-retained component.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * Hilt injects the @ActivityRetainedScoped bus.
     * The same instance is injected into all ViewModels in this Activity.
     */
    @Inject
    lateinit var navigationEventBus: NavigationEventBus

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            EnterpriseTheme {
                EnterpriseApp(navigationEventBus = navigationEventBus)
            }
        }
    }
}

@Composable
private fun EnterpriseApp(
    navigationEventBus: NavigationEventBus,
    modifier: Modifier = Modifier,
) {
    /**
     * AppNavHost is the ONLY place where NavController lives.
     * navigationEvents is collected here → NavController.navigate() is called here.
     * No ViewModel, no other composable ever touches NavController.
     */
    AppNavHost(
        navigationEvents = navigationEventBus.navigationEvents,
        modifier         = modifier,
    ) {
        // Registers all feature destinations via extension functions
        AppGraph()
    }
}