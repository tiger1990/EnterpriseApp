package com.enterprise.feature.settings.mvi

import com.enterprise.core.common.mvi.Reducer
import com.enterprise.core.common.mvi.UiAction
import com.enterprise.core.common.mvi.UiEffect
import com.enterprise.core.common.mvi.UiState
import com.enterprise.core.domain.model.Item

// ═══════════════════════════ MVI ═══════════════════════════════════════════════

data class SettingsState(
    val isDarkTheme: Boolean         = false,
    val isNotificationsEnabled: Boolean = true,
    val appVersion: String           = "1.0.0",
) : UiState

sealed interface SettingsAction : UiAction {
    data class DarkThemeToggled(val enabled: Boolean)         : SettingsAction
    data class NotificationsToggled(val enabled: Boolean)     : SettingsAction
    data object BackPressed                                    : SettingsAction
    data object ClearCacheClicked                              : SettingsAction
}

sealed interface SettingsEffect : UiEffect {
    data class ShowSnackbar(val message: String) : SettingsEffect
}

class SettingsReducer : Reducer<SettingsState, SettingsAction> {
    override fun reduce(state: SettingsState, action: SettingsAction): SettingsState = when (action) {
        is SettingsAction.DarkThemeToggled      -> state.copy(isDarkTheme = action.enabled)
        is SettingsAction.NotificationsToggled  -> state.copy(isNotificationsEnabled = action.enabled)
        else -> state
    }
}