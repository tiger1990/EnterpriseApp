package com.enterprise.feature.detail.mvi

import com.enterprise.core.common.mvi.Reducer
import com.enterprise.core.common.mvi.UiAction
import com.enterprise.core.common.mvi.UiEffect
import com.enterprise.core.common.mvi.UiState
import com.enterprise.core.domain.model.Item

// ─── State ────────────────────────────────────────────────────────────────────

data class DetailState(
    val itemId: String       = "",
    val itemTitle: String    = "",   // Shown immediately from nav args (no flicker)
    val item: Item?          = null,
    val isLoading: Boolean   = false,
    val errorMessage: String? = null,
) : UiState

// ─── Actions ──────────────────────────────────────────────────────────────────

sealed interface DetailAction : UiAction {
    data class Initialize(val itemId: String, val itemTitle: String) : DetailAction
    data object LoadItem          : DetailAction
    data object FavouriteToggled  : DetailAction
    data object BackPressed       : DetailAction
    data object ErrorDismissed    : DetailAction
    // Internal
    internal data class ItemLoaded(val item: Item)      : DetailAction
    internal data class LoadFailed(val message: String) : DetailAction
    internal data class FavouriteUpdated(val item: Item) : DetailAction
}

// ─── Effects ──────────────────────────────────────────────────────────────────

sealed interface DetailEffect : UiEffect {
    data class ShowSnackbar(val message: String) : DetailEffect
}

// ─── Reducer ──────────────────────────────────────────────────────────────────

class DetailReducer : Reducer<DetailState, DetailAction> {
    override fun reduce(state: DetailState, action: DetailAction): DetailState = when (action) {
        is DetailAction.Initialize -> state.copy(
            itemId    = action.itemId,
            itemTitle = action.itemTitle,
        )
        DetailAction.LoadItem -> state.copy(isLoading = true, errorMessage = null)
        is DetailAction.ItemLoaded -> state.copy(
            item      = action.item,
            isLoading = false,
        )
        is DetailAction.LoadFailed -> state.copy(
            isLoading    = false,
            errorMessage = action.message,
        )
        is DetailAction.FavouriteUpdated -> state.copy(item = action.item)
        DetailAction.ErrorDismissed -> state.copy(errorMessage = null)
        DetailAction.BackPressed, DetailAction.FavouriteToggled -> state
    }
}
