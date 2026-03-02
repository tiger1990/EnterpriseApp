package com.enterprise.feature.search.ui

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

// ═══════════════════════════ UI ═══════════════════════════════════════════════

@Composable
fun SearchScreen(viewModel: SearchViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SearchContent(state = state, onAction = viewModel::dispatch)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchContent(
    state: SearchState,
    onAction: (SearchAction) -> Unit,
) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            SearchBar(
                inputField = {
                    androidx.compose.material3.SearchBarDefaults.InputField(
                        query         = state.query,
                        onQueryChange = { onAction(SearchAction.QueryChanged(it)) },
                        onSearch      = { onAction(SearchAction.Search(it)) },
                        expanded      = state.isActive,
                        onExpandedChange = { onAction(SearchAction.ActiveChanged(it)) },
                        placeholder   = { Text("Search items...") },
                        leadingIcon   = { Icon(Icons.Default.Search, null) },
                        trailingIcon  = {
                            if (state.query.isNotEmpty()) {
                                IconButton(onClick = { onAction(SearchAction.ClearQuery) }) {
                                    Icon(Icons.Default.Clear, "Clear")
                                }
                            } else {
                                IconButton(onClick = { onAction(SearchAction.BackPressed) }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                                }
                            }
                        },
                    )
                },
                expanded  = state.isActive,
                onExpandedChange = { onAction(SearchAction.ActiveChanged(it)) },
                modifier  = Modifier.fillMaxWidth(),
            ) {
                // Search results inside expanded SearchBar
                SearchResults(items = state.results, onItemClick = { onAction(SearchAction.ResultClicked(it)) })
            }

            if (!state.isActive) {
                // Recent searches shown when bar is collapsed
                RecentSearches(
                    searches = state.recentSearches,
                    onSearchClick = { onAction(SearchAction.Search(it)) },
                )
            }
        }
    }
}

@Composable
private fun SearchResults(items: List<Item>, onItemClick: (Item) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
        items(items, key = { it.id }) { item ->
            ListItem(
                headlineContent = { Text(item.title) },
                supportingContent = { Text(item.description, maxLines = 1) },
                modifier = Modifier.clickable { onItemClick(item) },
            )
        }
    }
}

@Composable
private fun RecentSearches(searches: List<String>, onSearchClick: (String) -> Unit) {
    if (searches.isEmpty()) return
    Column(Modifier.padding(16.dp)) {
        Text("Recent", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
        searches.forEach { query ->
            ListItem(
                headlineContent = { Text(query) },
                leadingContent  = { Icon(Icons.Default.Search, null) },
                modifier        = Modifier.clickable { onSearchClick(query) },
            )
        }
    }
}