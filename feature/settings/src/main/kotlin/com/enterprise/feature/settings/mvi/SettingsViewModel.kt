package com.enterprise.feature.seettings.mvi

import androidx.lifecycle.SavedStateHandle
import com.enterprise.core.common.mvi.MviViewModel
import com.enterprise.core.common.result.Result
import com.enterprise.core.navigation.DetailRoute
import com.enterprise.core.navigation.NavigationEvent
import com.enterprise.core.navigation.NavigationEventBus
import com.enterprise.core.navigation.ProfileRoute
import com.enterprise.core.navigation.SearchRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

// ═══════════════════════════ ViewModel ════════════════════════════════════════

@HiltViewModel
class SettingsViewModel @Inject constructor(
    navigationBus: NavigationEventBus,
    savedStateHandle: SavedStateHandle,
) : MviViewModel<SettingsState, SettingsAction, SettingsEffect>(
    initialState = SettingsState(
        isDarkTheme = savedStateHandle.getOrDefault(KEY_DARK_THEME, false),
        isNotificationsEnabled = savedStateHandle.getOrDefault(KEY_NOTIFICATIONS, true),
    ),
    reducer = SettingsReducer(),
    navigationBus = navigationBus,
    savedStateHandle = savedStateHandle,
) {
    override suspend fun handleAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.DarkThemeToggled -> {
                savedStateHandle.saveTo(KEY_DARK_THEME, action.enabled)
            }
            is SettingsAction.NotificationsToggled -> {
                savedStateHandle.saveTo(KEY_NOTIFICATIONS, action.enabled)
            }
            SettingsAction.BackPressed    -> navigate(NavigationEvent.NavigateUp)
            SettingsAction.ClearCacheClicked -> emitEffect(SettingsEffect.ShowSnackbar("Cache cleared"))
            else -> Unit
        }
    }

    companion object {
        private const val KEY_DARK_THEME     = "settings_dark_theme"
        private const val KEY_NOTIFICATIONS  = "settings_notifications"
    }
}