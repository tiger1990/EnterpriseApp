package com.enterprise.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.composable
import com.enterprise.core.common.mvi.MviViewModel
import com.enterprise.core.common.mvi.Reducer
import com.enterprise.core.common.mvi.UiAction
import com.enterprise.core.common.mvi.UiEffect
import com.enterprise.core.common.mvi.UiState
import com.enterprise.core.navigation.NavGraphBuilderScope
import com.enterprise.core.navigation.NavigationEvent
import com.enterprise.core.navigation.NavigationEventBus
import com.enterprise.core.navigation.SettingsRoute
import com.enterprise.feature.seettings.mvi.SettingsViewModel
import com.enterprise.feature.settings.mvi.SettingsAction
import com.enterprise.feature.settings.mvi.SettingsState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// ═══════════════════════════ UI ═══════════════════════════════════════════════

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsContent(state = state, onAction = viewModel::dispatch)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsContent(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { onAction(SettingsAction.BackPressed) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            SettingsSection(title = "Appearance") {
                ListItem(
                    headlineContent   = { Text("Dark Theme") },
                    supportingContent = { Text("Toggle dark/light mode") },
                    trailingContent   = {
                        Switch(
                            checked         = state.isDarkTheme,
                            onCheckedChange = { onAction(SettingsAction.DarkThemeToggled(it)) },
                        )
                    },
                )
            }

            HorizontalDivider()

            SettingsSection(title = "Notifications") {
                ListItem(
                    headlineContent   = { Text("Push Notifications") },
                    supportingContent = { Text("Receive app notifications") },
                    trailingContent   = {
                        Switch(
                            checked         = state.isNotificationsEnabled,
                            onCheckedChange = { onAction(SettingsAction.NotificationsToggled(it)) },
                        )
                    },
                )
            }

            HorizontalDivider()

            SettingsSection(title = "Storage") {
                ListItem(
                    headlineContent = { Text("Clear Cache") },
                    modifier        = Modifier.clickable { onAction(SettingsAction.ClearCacheClicked) },
                )
            }

            HorizontalDivider()

            ListItem(
                headlineContent   = { Text("App Version") },
                trailingContent   = {
                    Text(state.appVersion, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline)
                },
            )
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text     = title,
            style    = MaterialTheme.typography.labelMedium,
            color    = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
        content()
    }
}