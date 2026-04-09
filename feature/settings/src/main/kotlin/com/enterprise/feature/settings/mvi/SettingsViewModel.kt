package com.enterprise.feature.settings.mvi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.enterprise.core.common.mvi.MviViewModel
import com.enterprise.core.common.mvi.getOrDefault
import com.enterprise.core.common.mvi.saveTo
import com.enterprise.core.domain.repository.ThemeRepository
import com.enterprise.core.navigation.NavigationEvent
import com.enterprise.core.navigation.NavigationEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

// ═══════════════════════════ ViewModel ════════════════════════════════════════

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
    navigationBus: NavigationEventBus,
    savedStateHandle: SavedStateHandle,
) : MviViewModel<SettingsState, SettingsAction, SettingsEffect>(
    initialState     = SettingsState(),
    reducer          = SettingsReducer(),
    navigationBus    = navigationBus,
    savedStateHandle = savedStateHandle,
) {
    // Settings have no repository backing — SavedStateHandle is the sole source of
    // truth for isDarkTheme and isNotificationsEnabled across process death.
    // persistState() fires synchronously after every reduce, guaranteeing durability
    // even if the process is killed before handleAction() runs.
    override fun restorePersistedState(initial: SettingsState): SettingsState =
        initial.copy(
            isDarkTheme            = savedStateHandle.getOrDefault(KEY_DARK_THEME, false),
            isNotificationsEnabled = savedStateHandle.getOrDefault(KEY_NOTIFICATIONS, true),
        )

    override fun persistState(state: SettingsState) {
        savedStateHandle.saveTo(KEY_DARK_THEME, state.isDarkTheme)
        savedStateHandle.saveTo(KEY_NOTIFICATIONS, state.isNotificationsEnabled)
    }

    init {
        // After process death, SavedStateHandle restores isDarkTheme correctly into
        // SettingsState, but ThemeRepository resets to false (in-memory only).
        // Re-sync on startup so EnterpriseTheme reflects the persisted preference immediately.
        viewModelScope.launch {
            themeRepository.setDarkTheme(state.value.isDarkTheme)
        }
    }

    override suspend fun handleAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.DarkThemeToggled      -> themeRepository.setDarkTheme(action.enabled)
            is SettingsAction.NotificationsToggled  -> Unit // persisted automatically via persistState()
            SettingsAction.BackPressed       -> navigate(NavigationEvent.NavigateUp)
            SettingsAction.ClearCacheClicked -> emitEffect(SettingsEffect.ShowSnackbar("Cache cleared"))
        }
    }

    companion object {
        private const val KEY_DARK_THEME     = "settings_dark_theme"
        private const val KEY_NOTIFICATIONS  = "settings_notifications"
    }
}