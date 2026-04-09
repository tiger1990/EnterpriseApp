package com.enterprise.feature.search.mvi

import com.enterprise.core.common.mvi.Reducer
import com.enterprise.core.common.mvi.UiAction
import com.enterprise.core.common.mvi.UiEffect
import com.enterprise.core.common.mvi.UiState


// ═══════════════════════════ MVI ═══════════════════════════════════════════════

data class SearchState(
    val query: String = "",
    val isActive: Boolean = false,
    val isSearching: Boolean = false,
    val results: List<SearchItemUiModel> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val errorMessage: String? = null,
) : UiState

sealed interface SearchAction : UiAction {
    data class QueryChanged(val query: String) : SearchAction
    data class Search(val query: String) : SearchAction
    data class ResultClicked(val itemId: String, val itemTitle: String) : SearchAction
    data object BackPressed : SearchAction
    data object ClearQuery : SearchAction
    data class ActiveChanged(val active: Boolean) : SearchAction

    // Internal — already mapped to UiModel before dispatch
    data class ResultsLoaded(val results: List<SearchItemUiModel>) : SearchAction
    data class SearchFailed(val message: String) : SearchAction
    data class RecentSearchesUpdated(val searches: List<String>) : SearchAction
}

sealed interface SearchEffect : UiEffect {
    data class ShowError(val message: String) : SearchEffect
}

class SearchReducer : Reducer<SearchState, SearchAction> {
    override fun reduce(state: SearchState, action: SearchAction): SearchState = when (action) {
        is SearchAction.QueryChanged -> state.copy(query = action.query)
        is SearchAction.Search -> state.copy(isSearching = true, errorMessage = null)
        is SearchAction.ActiveChanged -> state.copy(isActive = action.active)
        SearchAction.ClearQuery -> state.copy(query = "", results = emptyList())
        is SearchAction.ResultsLoaded -> state.copy(
            results = action.results,
            isSearching = false,
        )

        is SearchAction.SearchFailed -> state.copy(
            isSearching = false,
            errorMessage = action.message,
        )

        is SearchAction.RecentSearchesUpdated -> state.copy(recentSearches = action.searches)

        is SearchAction.ResultClicked,
        SearchAction.BackPressed -> state
    }
}