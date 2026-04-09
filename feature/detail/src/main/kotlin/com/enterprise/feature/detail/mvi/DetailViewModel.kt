package com.enterprise.feature.detail.mvi

import androidx.lifecycle.SavedStateHandle
import com.enterprise.core.common.mvi.MviViewModel
import com.enterprise.core.common.result.Result
import com.enterprise.core.domain.usecase.GetItemUseCase
import com.enterprise.core.domain.usecase.ToggleFavouriteUseCase
import com.enterprise.core.navigation.NavigationEvent
import com.enterprise.core.navigation.NavigationEventBus
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

// ─── ViewModel ────────────────────────────────────────────────────────────────

/**
 * DetailViewModel receives route args via @AssistedInject — the Nav3-idiomatic
 * pattern where the entry<K> lambda provides the typed key to the factory.
 *
 * This removes the Nav2 `savedStateHandle.toRoute<DetailRoute>()` dependency,
 * which was semantically incorrect in a Nav3 app and fragile across library updates.
 *
 * Process death: route args come from the restored back stack key (not SavedStateHandle),
 * so they are always correct even after the process is killed.
 */
@HiltViewModel(assistedFactory = DetailViewModel.Factory::class)
class DetailViewModel @AssistedInject constructor(
    @Assisted("itemId") val itemId: String,
    @Assisted("itemTitle") val itemTitle: String,
    private val getItem: GetItemUseCase,
    private val toggleFavourite: ToggleFavouriteUseCase,
    navigationBus: NavigationEventBus,
    savedStateHandle: SavedStateHandle,
) : MviViewModel<DetailState, DetailAction, DetailEffect>(
    initialState     = DetailState(itemId = itemId, itemTitle = itemTitle),
    reducer          = DetailReducer(),
    navigationBus    = navigationBus,
    savedStateHandle = savedStateHandle,
) {
    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("itemId") itemId: String,
            @Assisted("itemTitle") itemTitle: String
        ): DetailViewModel
    }

    init {
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