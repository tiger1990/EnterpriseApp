package com.enterprise.feature.profile.mvi

import androidx.lifecycle.SavedStateHandle
import com.enterprise.core.common.mvi.MviViewModel
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
    init { dispatch(ProfileAction.LoadProfile) }

    override suspend fun handleAction(action: ProfileAction) {
        when (action) {
            ProfileAction.LoadProfile -> {
                when (val result = getProfile()) {
                    is Result.Success -> dispatch(ProfileAction.ProfileLoaded(result.data))
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