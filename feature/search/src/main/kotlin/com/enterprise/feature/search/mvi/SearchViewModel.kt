package com.enterprise.feature.search.mvi

import androidx.lifecycle.SavedStateHandle
import com.enterprise.core.common.mvi.MviViewModel
import com.enterprise.core.common.result.Result
import com.enterprise.core.navigation.DetailRoute
import com.enterprise.core.navigation.NavigationEvent
import com.enterprise.core.navigation.NavigationEventBus
import com.enterprise.core.navigation.ProfileRoute
import com.enterprise.core.navigation.SearchRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


// ═══════════════════════════ ViewModel ════════════════════════════════════════

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchItems: SearchItemsUseCase,
    navigationBus: NavigationEventBus,
    savedStateHandle: SavedStateHandle,
) : MviViewModel<SearchState, SearchAction, SearchEffect>(
    initialState     = SearchState(
        query = savedStateHandle.getOrDefault(KEY_QUERY, ""),
    ),
    reducer          = SearchReducer(),
    navigationBus    = navigationBus,
    savedStateHandle = savedStateHandle,
) {
    override suspend fun handleAction(action: SearchAction) {
        when (action) {
            is SearchAction.Search -> {
                if (action.query.isBlank()) return
                savedStateHandle.saveTo(KEY_QUERY, action.query)
                when (val result = searchItems(SearchItemsUseCase.Params(action.query))) {
                    is Result.Success -> dispatch(SearchAction.ResultsLoaded(result.data))
                    is Result.Error   -> dispatch(SearchAction.SearchFailed(result.exception.message ?: "Search failed"))
                    else -> Unit
                }
            }
            is SearchAction.ResultClicked -> navigate(
                NavigationEvent.NavigateTo(
                    DetailRoute(action.item.id, action.item.title)
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