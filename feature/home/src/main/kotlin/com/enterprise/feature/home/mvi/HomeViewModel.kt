package com.enterprise.feature.home.mvi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.enterprise.core.common.mvi.MviViewModel
import com.enterprise.core.common.mvi.getOrDefault
import com.enterprise.core.common.mvi.saveTo
import com.enterprise.core.common.result.Result
import com.enterprise.core.domain.usecase.ObserveItemsUseCase
import com.enterprise.core.domain.usecase.ToggleFavouriteUseCase
import com.enterprise.core.navigation.DetailRoute
import com.enterprise.core.navigation.NavigationEvent
import com.enterprise.core.navigation.NavigationEventBus
import com.enterprise.core.navigation.ProfileRoute
import com.enterprise.core.navigation.SearchRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * HomeViewModel
 *
 * ─── Process Death Restoration ───────────────────────────────────────────────
 * SavedStateHandle stores lightweight UI state (selectedTabIndex).
 * Items are re-loaded from repository (in-memory cache / local DB / network).
 * We never serialize the full item list into SavedState — that's repository's job.
 *
 * ─── Navigation ──────────────────────────────────────────────────────────────
 * Does NOT hold NavController.
 * Emits NavigationEvents via [navigationBus] → collected by AppNavHost.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val observeItems: ObserveItemsUseCase,
    private val toggleFavourite: ToggleFavouriteUseCase,
    navigationBus: NavigationEventBus,
    savedStateHandle: SavedStateHandle,
) : MviViewModel<HomeState, HomeAction, HomeEffect>(
    initialState  = HomeState(
        // Restore tab selection after process death
        selectedTabIndex = savedStateHandle.getOrDefault(KEY_TAB, 0),
    ),
    reducer       = HomeReducer(),
    navigationBus = navigationBus,
    savedStateHandle = savedStateHandle,
) {
    init {
        // Start observing items immediately
        dispatch(HomeAction.LoadItems)
        observeItemsStream()
    }

    // ── Middleware ─────────────────────────────────────────────────────────────
    override suspend fun handleAction(action: HomeAction) {
        when (action) {
            HomeAction.LoadItems, HomeAction.RefreshItems -> {
                // observeItemsStream() handles continuous updates;
                // refreshItems triggers a forced network fetch if needed
            }

            is HomeAction.ItemClicked -> navigate(
                NavigationEvent.NavigateTo(
                    route = DetailRoute(
                        itemId    = action.item.id,
                        itemTitle = action.item.title,
                    ),
                )
            )

            HomeAction.SearchClicked -> navigate(
                NavigationEvent.NavigateTo(SearchRoute)
            )

            HomeAction.ProfileClicked -> navigate(
                NavigationEvent.NavigateTo(ProfileRoute)
            )

            is HomeAction.FavouriteToggled -> {
                when (val result = toggleFavourite(action.itemId)) {
                    is Result.Success -> dispatch(HomeAction.FavouriteUpdated(result.data))
                    is Result.Error   -> emitEffect(HomeEffect.ShowSnackbar("Failed to update favourite"))
                    else -> Unit
                }
            }

            is HomeAction.TabSelected -> {
                // Persist tab selection to survive process death
                savedStateHandle.saveTo(KEY_TAB, action.index)
            }

            else -> Unit // State-only actions handled entirely by reducer
        }
    }

    // ── Private helpers ────────────────────────────────────────────────────────
    private fun observeItemsStream() {
        observeItems(Unit).onEach { result ->
            when (result) {
                is Result.Success -> dispatch(HomeAction.ItemsLoaded(result.data))
                is Result.Error   -> dispatch(HomeAction.ItemsLoadFailed(result.exception.message ?: "Unknown error"))
                Result.Loading    -> { /* initial loading state set by LoadItems action */ }
            }
        }.launchIn(viewModelScope)
    }

    companion object {
        private const val KEY_TAB = "home_selected_tab"
    }
}
