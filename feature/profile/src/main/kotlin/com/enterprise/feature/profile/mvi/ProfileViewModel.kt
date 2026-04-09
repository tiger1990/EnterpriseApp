package com.enterprise.feature.profile.mvi

import androidx.lifecycle.SavedStateHandle
import com.enterprise.core.common.mvi.ActionConcurrency
import com.enterprise.core.common.mvi.MviViewModel
import com.enterprise.feature.profile.mvi.toUiModel
import com.enterprise.core.common.result.Result
import com.enterprise.core.domain.usecase.GetProfileUseCase
import com.enterprise.core.navigation.EditProfileRoute
import com.enterprise.core.navigation.NavigationEvent
import com.enterprise.core.navigation.NavigationEventBus
import com.enterprise.core.navigation.SettingsRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfile: GetProfileUseCase,
    navigationBus: NavigationEventBus,
    savedStateHandle: SavedStateHandle,
) : MviViewModel<ProfileState, ProfileAction, ProfileEffect>(
    initialState     = ProfileState(),
    reducer          = ProfileReducer(),
    navigationBus    = navigationBus,
    savedStateHandle = savedStateHandle,
) {
    // ─── Action concurrency ───────────────────────────────────────────────────
    // LoadProfile: idempotent — init{} dispatches it once but guard against any
    //   future pull-to-refresh additions triggering a concurrent fetch.
    // EditProfileTapped: navigation — drop prevents double-tap double-push.
    override fun actionConcurrency(action: ProfileAction) = when (action) {
        ProfileAction.LoadProfile     -> ActionConcurrency.DropIfBusy("load")
        ProfileAction.EditProfileTapped -> ActionConcurrency.DropIfBusy("nav_edit")
        else                          -> ActionConcurrency.Concurrent
    }

    init { dispatch(ProfileAction.LoadProfile) }

    override suspend fun handleAction(action: ProfileAction) {
        when (action) {
            ProfileAction.LoadProfile -> {
                when (val result = getProfile()) {
                    is Result.Success -> dispatch(ProfileAction.ProfileLoaded(result.data.toUiModel()))
                    is Result.Error   -> dispatch(ProfileAction.LoadFailed(result.exception.message ?: "Error"))
                    else -> Unit
                }
            }
            ProfileAction.EditProfileTapped -> {
                val userId = state.value.profile?.id ?: return
                navigate(NavigationEvent.NavigateTo(EditProfileRoute(userId)))
            }
            ProfileAction.SettingsTapped -> navigate(NavigationEvent.NavigateTo(SettingsRoute))
            ProfileAction.BackPressed    -> navigate(NavigationEvent.NavigateUp)
            else -> Unit
        }
    }
}