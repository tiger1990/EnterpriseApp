package com.enterprise.feature.home.mvi

import com.enterprise.core.common.mvi.Reducer
import com.enterprise.core.common.mvi.UiAction
import com.enterprise.core.common.mvi.UiEffect
import com.enterprise.core.common.mvi.UiState
import com.enterprise.core.domain.model.Item

// ─── State ────────────────────────────────────────────────────────────────────

data class HomeState(
    val items: List<Item> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val selectedTabIndex: Int = 0,
) : UiState {
    // Derived state — computed, not stored
    val favourites: List<Item> get() = items.filter { it.isFavourite }
    val isEmpty: Boolean get() = !isLoading && items.isEmpty() && errorMessage == null
}

// ─── Actions ──────────────────────────────────────────────────────────────────

sealed interface HomeAction : UiAction {
    data object LoadItems : HomeAction
    data object RefreshItems : HomeAction
    data class ItemClicked(val item: Item) : HomeAction
    data class FavouriteToggled(val itemId: String) : HomeAction
    data object SearchClicked : HomeAction
    data object ProfileClicked : HomeAction
    data class TabSelected(val index: Int) : HomeAction
    data object ErrorDismissed : HomeAction

    // Internal — emitted by middleware after async operations
    data class ItemsLoaded(val items: List<Item>) : HomeAction
    data class ItemsLoadFailed(val message: String) : HomeAction
    data class FavouriteUpdated(val item: Item) : HomeAction
}

// ─── Effects ──────────────────────────────────────────────────────────────────

sealed interface HomeEffect : UiEffect {
    data class ShowSnackbar(val message: String) : HomeEffect
}

// ─── Reducer (pure function — zero side effects) ──────────────────────────────

class HomeReducer : Reducer<HomeState, HomeAction> {
    override fun reduce(state: HomeState, action: HomeAction): HomeState = when (action) {
        HomeAction.LoadItems -> state.copy(
            isLoading = state.items.isEmpty(),
            errorMessage = null,
        )

        HomeAction.RefreshItems -> state.copy(
            isRefreshing = true,
            errorMessage = null,
        )

        is HomeAction.ItemsLoaded -> state.copy(
            items = action.items,
            isLoading = false,
            isRefreshing = false,
            errorMessage = null,
        )

        is HomeAction.ItemsLoadFailed -> state.copy(
            isLoading = false,
            isRefreshing = false,
            errorMessage = action.message,
        )

        is HomeAction.FavouriteUpdated -> state.copy(
            items = state.items.map { if (it.id == action.item.id) action.item else it },
        )

        is HomeAction.TabSelected -> state.copy(selectedTabIndex = action.index)

        HomeAction.ErrorDismissed -> state.copy(errorMessage = null)

        // Navigation actions don't mutate state
        is HomeAction.ItemClicked,
        HomeAction.SearchClicked,
        HomeAction.ProfileClicked,
        is HomeAction.FavouriteToggled -> state
    }
}