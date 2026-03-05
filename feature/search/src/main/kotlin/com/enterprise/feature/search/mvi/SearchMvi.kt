package com.enterprise.feature.search.mvi

import com.enterprise.core.common.mvi.Reducer
import com.enterprise.core.common.mvi.UiAction
import com.enterprise.core.common.mvi.UiEffect
import com.enterprise.core.common.mvi.UiState
import com.enterprise.core.domain.model.Item
import com.enterprise.core.domain.model.SearchResult

// ═══════════════════════════ MVI ═══════════════════════════════════════════════

data class SearchState(
    val query: String = "",
    val isActive: Boolean = false,
    val isSearching: Boolean = false,
    val results: List<Item> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val errorMessage: String? = null,
) : UiState

sealed interface SearchAction : UiAction {
    data class QueryChanged(val query: String) : SearchAction
    data class Search(val query: String) : SearchAction
    data class ResultClicked(val item: Item) : SearchAction
    data object BackPressed : SearchAction
    data object ClearQuery : SearchAction
    data class ActiveChanged(val active: Boolean) : SearchAction

    // Internal
    data class ResultsLoaded(val result: SearchResult) : SearchAction
    data class SearchFailed(val message: String) : SearchAction
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
            results = action.result.items,
            isSearching = false,
        )

        is SearchAction.SearchFailed -> state.copy(
            isSearching = false,
            errorMessage = action.message,
        )

        is SearchAction.ResultClicked,
        SearchAction.BackPressed -> state
    }
}