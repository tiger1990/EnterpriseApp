package com.enterprise.feature.home.mvi


// ═══════════════════════════ ViewModel ════════════════════════════════════════

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