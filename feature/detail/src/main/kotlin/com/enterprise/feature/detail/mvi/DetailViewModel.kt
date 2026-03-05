package com.enterprise.feature.detail.mvi

import androidx.lifecycle.SavedStateHandle
import com.enterprise.core.common.mvi.MviViewModel
import com.enterprise.core.common.mvi.getOrDefault
import com.enterprise.core.common.result.Result
import com.enterprise.core.domain.usecase.GetItemUseCase
import com.enterprise.core.domain.usecase.ToggleFavouriteUseCase
import com.enterprise.core.navigation.DetailRoute
import com.enterprise.core.navigation.NavigationEvent
import com.enterprise.core.navigation.NavigationEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// ─── ViewModel ────────────────────────────────────────────────────────────────

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getItem: GetItemUseCase,
    private val toggleFavourite: ToggleFavouriteUseCase,
    navigationBus: NavigationEventBus,
    savedStateHandle: SavedStateHandle,
) : MviViewModel<DetailState, DetailAction, DetailEffect>(
    initialState  = DetailState(
        // Restore from SavedState after process death — args are always saved by Nav3
        itemId    = savedStateHandle.getOrDefault(DetailRoute::itemId.name, ""),
        itemTitle = savedStateHandle.getOrDefault(DetailRoute::itemTitle.name, ""),
    ),
    reducer       = DetailReducer(),
    navigationBus = navigationBus,
    savedStateHandle = savedStateHandle,
) {
    init {
        // Nav3 automatically puts route args into SavedStateHandle
        val itemId    = savedStateHandle.getOrDefault(DetailRoute::itemId.name, "")
        val itemTitle = savedStateHandle.getOrDefault(DetailRoute::itemTitle.name, "")
        dispatch(DetailAction.Initialize(itemId, itemTitle))
        dispatch(DetailAction.LoadItem)
    }

    override suspend fun handleAction(action: DetailAction) {
        when (action) {
            DetailAction.LoadItem -> {
                when (val result = getItem(state.value.itemId)) {
                    is Result.Success -> dispatch(DetailAction.ItemLoaded(result.data))
                    is Result.Error   -> dispatch(DetailAction.LoadFailed(result.exception.message ?: "Error"))
                    else -> Unit
                }
            }

            DetailAction.FavouriteToggled -> {
                val itemId = state.value.item?.id ?: return
                when (val result = toggleFavourite(itemId)) {
                    is Result.Success -> dispatch(DetailAction.FavouriteUpdated(result.data))
                    is Result.Error   -> emitEffect(DetailEffect.ShowSnackbar("Failed to toggle favourite"))
                    else -> Unit
                }
            }

            DetailAction.BackPressed -> navigate(NavigationEvent.NavigateUp)

            else -> Unit
        }
    }
}