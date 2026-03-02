package com.enterprise.feature.search.mvi

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.composable
import com.enterprise.core.common.mvi.MviViewModel
import com.enterprise.core.common.mvi.Reducer
import com.enterprise.core.common.mvi.UiAction
import com.enterprise.core.common.mvi.UiEffect
import com.enterprise.core.common.mvi.UiState
import com.enterprise.core.common.result.Result
import com.enterprise.core.domain.model.Item
import com.enterprise.core.domain.model.SearchResult
import com.enterprise.core.domain.usecase.SearchItemsUseCase
import com.enterprise.core.navigation.DetailRoute
import com.enterprise.core.navigation.NavGraphBuilderScope
import com.enterprise.core.navigation.NavigationEvent
import com.enterprise.core.navigation.NavigationEventBus
import com.enterprise.core.navigation.SearchRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// ═══════════════════════════ MVI ═══════════════════════════════════════════════

data class SearchState(
    val query: String           = "",
    val isActive: Boolean       = false,
    val isSearching: Boolean    = false,
    val results: List<Item>     = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val errorMessage: String?   = null,
) : UiState

sealed interface SearchAction : UiAction {
    data class QueryChanged(val query: String) : SearchAction
    data class Search(val query: String)       : SearchAction
    data class ResultClicked(val item: Item)   : SearchAction
    data object BackPressed                    : SearchAction
    data object ClearQuery                     : SearchAction
    data class ActiveChanged(val active: Boolean) : SearchAction
    // Internal
    internal data class ResultsLoaded(val result: SearchResult) : SearchAction
    internal data class SearchFailed(val message: String)       : SearchAction
}

sealed interface SearchEffect : UiEffect {
    data class ShowError(val message: String) : SearchEffect
}

class SearchReducer : Reducer<SearchState, SearchAction> {
    override fun reduce(state: SearchState, action: SearchAction): SearchState = when (action) {
        is SearchAction.QueryChanged -> state.copy(query = action.query)
        is SearchAction.Search       -> state.copy(isSearching = true, errorMessage = null)
        is SearchAction.ActiveChanged -> state.copy(isActive = action.active)
        SearchAction.ClearQuery      -> state.copy(query = "", results = emptyList())
        is SearchAction.ResultsLoaded -> state.copy(
            results     = action.result.items,
            isSearching = false,
        )
        is SearchAction.SearchFailed -> state.copy(
            isSearching  = false,
            errorMessage = action.message,
        )
        is SearchAction.ResultClicked,
        SearchAction.BackPressed -> state
    }
}