package com.enterprise.feature.search.mvi

import androidx.lifecycle.SavedStateHandle
import com.enterprise.core.common.mvi.ActionConcurrency
import com.enterprise.core.common.mvi.MviViewModel
import com.enterprise.core.common.mvi.getOrDefault
import com.enterprise.core.common.mvi.saveTo
import com.enterprise.core.common.result.Result
import androidx.lifecycle.viewModelScope
import com.enterprise.core.domain.repository.SearchRepository
import com.enterprise.core.domain.usecase.SearchItemsUseCase
import com.enterprise.core.navigation.DetailRoute
import com.enterprise.core.navigation.NavigationEvent
import com.enterprise.core.navigation.NavigationEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


// ═══════════════════════════ ViewModel ════════════════════════════════════════

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchItems: SearchItemsUseCase,
    private val searchRepository: SearchRepository,
    navigationBus: NavigationEventBus,
    savedStateHandle: SavedStateHandle,
) : MviViewModel<SearchState, SearchAction, SearchEffect>(
    initialState     = SearchState(),
    reducer          = SearchReducer(),
    navigationBus    = navigationBus,
    savedStateHandle = savedStateHandle,
) {
    // ─── Action concurrency ───────────────────────────────────────────────────
    // Search: cancel the in-flight network call and start fresh with the new query.
    // Only the latest query result is relevant; prior results would overwrite correct state.
    override fun actionConcurrency(action: SearchAction) = when (action) {
        is SearchAction.Search -> ActionConcurrency.CancelAndRelaunch("search")
        else                   -> ActionConcurrency.Concurrent
    }

    // query is persisted on every state change (covers QueryChanged, ClearQuery, and Search)
    override fun restorePersistedState(initial: SearchState): SearchState =
        initial.copy(query = savedStateHandle.getOrDefault(KEY_QUERY, ""))

    override fun persistState(state: SearchState) {
        savedStateHandle.saveTo(KEY_QUERY, state.query)
    }

    init {
        // Keep recentSearches in sync with the repository stream
        searchRepository.observeRecentSearches()
            .onEach { dispatch(SearchAction.RecentSearchesUpdated(it)) }
            .launchIn(viewModelScope)
    }

    override suspend fun handleAction(action: SearchAction) {
        when (action) {
            is SearchAction.Search -> {
                if (action.query.isBlank()) return
                when (val result = searchItems(SearchItemsUseCase.Params(action.query))) {
                    is Result.Success -> {
                        searchRepository.saveRecentSearch(action.query)
                        dispatch(SearchAction.ResultsLoaded(result.data.items.map { it.toSearchUiModel() }))
                    }
                    is Result.Error   -> dispatch(SearchAction.SearchFailed(result.exception.message ?: "Search failed"))
                    else -> Unit
                }
            }
            is SearchAction.ResultClicked -> navigate(
                NavigationEvent.NavigateTo(
                    DetailRoute(action.itemId, action.itemTitle)
                )
            )
            SearchAction.BackPressed -> navigate(NavigationEvent.NavigateUp)
            else -> Unit
        }
    }

    companion object {
        private const val KEY_QUERY = "search_query"
    }
}