package com.enterprise.feature.home.mvi

import com.enterprise.core.common.mvi.Reducer
import com.enterprise.core.common.mvi.UiAction
import com.enterprise.core.common.mvi.UiEffect
import com.enterprise.core.common.mvi.UiState

// ─── State ────────────────────────────────────────────────────────────────────

data class HomeState(
    val items: List<HomeItemUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val selectedTabIndex: Int = 0,
) : UiState {
    // Derived state — computed, not stored
    val favourites: List<HomeItemUiModel> get() = items.filter { it.isFavourite }
    val isEmpty: Boolean get() = !isLoading && !isRefreshing && items.isEmpty() && errorMessage == null
}

// ─── Actions ──────────────────────────────────────────────────────────────────

sealed interface HomeAction : UiAction {
    data object LoadItems : HomeAction
    data object RefreshItems : HomeAction
    data class ItemClicked(val itemId: String, val itemTitle: String) : HomeAction
    data class FavouriteToggled(val itemId: String) : HomeAction
    data object SearchClicked : HomeAction
    data object ProfileClicked : HomeAction
    data class TabSelected(val index: Int) : HomeAction
    data object ErrorDismissed : HomeAction

    // Internal — emitted by middleware after async operations, already mapped to UiModel
    data class ItemsLoaded(val items: List<HomeItemUiModel>) : HomeAction
    data class ItemsLoadFailed(val message: String) : HomeAction
    data class FavouriteUpdated(val item: HomeItemUiModel) : HomeAction
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

        // Optimistic update: flip isFavourite immediately so the UI responds to the tap
        // without waiting for the async toggleFavourite call or the stream re-emission.
        // FavouriteUpdated (on success) and ItemsLoaded (from the stream) will confirm the
        // correct server state. On error, FavouriteUpdated is not dispatched and the
        // stream re-emits the reverted value — the UI self-corrects automatically.
        is HomeAction.FavouriteToggled -> state.copy(
            items = state.items.map { item ->
                if (item.id != action.itemId) item
                else {
                    val toggled = !item.isFavourite
                    item.copy(
                        isFavourite = toggled,
                        favouriteContentDescription = if (toggled) "Remove from favourites" else "Add to favourites",
                    )
                }
            }
        )

        // Navigation actions don't mutate state
        is HomeAction.ItemClicked,
        HomeAction.SearchClicked,
        HomeAction.ProfileClicked -> state
    }
}