package com.enterprise.feature.profile.mvi

import com.enterprise.core.common.mvi.Reducer
import com.enterprise.core.common.mvi.UiAction
import com.enterprise.core.common.mvi.UiEffect
import com.enterprise.core.common.mvi.UiState
import com.enterprise.core.domain.model.Item

// ═══════════════════════════ MVI ═══════════════════════════════════════════════

data class ProfileState(
    val profile: UserProfile? = null,
    val isLoading: Boolean    = false,
    val errorMessage: String? = null,
) : UiState

sealed interface ProfileAction : UiAction {
    data object LoadProfile      : ProfileAction
    data object EditProfileTapped : ProfileAction
    data object SettingsTapped   : ProfileAction
    data object BackPressed      : ProfileAction
    internal data class ProfileLoaded(val profile: UserProfile) : ProfileAction
    internal data class LoadFailed(val message: String)         : ProfileAction
}

sealed interface ProfileEffect : UiEffect

class ProfileReducer : Reducer<ProfileState, ProfileAction> {
    override fun reduce(state: ProfileState, action: ProfileAction): ProfileState = when (action) {
        ProfileAction.LoadProfile -> state.copy(isLoading = true)
        is ProfileAction.ProfileLoaded -> state.copy(profile = action.profile, isLoading = false)
        is ProfileAction.LoadFailed    -> state.copy(errorMessage = action.message, isLoading = false)
        else -> state
    }
}
