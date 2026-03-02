package com.enterprise.feature.home.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.enterprise.core.domain.model.Item
import com.enterprise.feature.home.mvi.HomeAction
import com.enterprise.feature.home.mvi.HomeEffect
import com.enterprise.feature.home.mvi.HomeState
import com.enterprise.feature.home.mvi.HomeViewModel

// ─── Entry point: wires ViewModel to stateless UI ─────────────────────────────

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect one-shot effects
    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is HomeEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    HomeContent(
        state          = state,
        snackbarHost   = snackbarHostState,
        onAction       = viewModel::dispatch,
    )
}

// ─── Stateless content composable (testable / previewable) ────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeContent(
    state: HomeState,
    snackbarHost: SnackbarHostState,
    onAction: (HomeAction) -> Unit,
) {
    Scaffold(
        topBar = {
            HomeTopBar(
                onSearchClick  = { onAction(HomeAction.SearchClicked) },
                onProfileClick = { onAction(HomeAction.ProfileClicked) },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHost) },
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            HomeTabs(
                selectedIndex = state.selectedTabIndex,
                onTabSelected = { onAction(HomeAction.TabSelected(it)) },
            )

            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh    = { onAction(HomeAction.RefreshItems) },
                modifier     = Modifier.fillMaxSize(),
            ) {
                AnimatedContent(
                    targetState = state,
                    label       = "home_content",
                ) { s ->
                    when {
                        s.isLoading          -> LoadingState()
                        s.errorMessage != null -> ErrorState(
                            message   = s.errorMessage,
                            onDismiss = { onAction(HomeAction.ErrorDismissed) },
                        )
                        s.isEmpty            -> EmptyState()
                        else -> ItemList(
                            items    = if (s.selectedTabIndex == 1) s.favourites else s.items,
                            onItemClick       = { onAction(HomeAction.ItemClicked(it)) },
                            onFavouriteToggle = { onAction(HomeAction.FavouriteToggled(it)) },
                        )
                    }
                }
            }
        }
    }
}

// ─── Sub-composables ──────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    TopAppBar(
        title = { Text("Discover") },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
            IconButton(onClick = onProfileClick) {
                Icon(Icons.Default.Person, contentDescription = "Profile")
            }
        },
    )
}

@Composable
private fun HomeTabs(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
) {
    val tabs = listOf("All", "Favourites")
    ScrollableTabRow(selectedTabIndex = selectedIndex) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedIndex == index,
                onClick  = { onTabSelected(index) },
                text     = { Text(title) },
            )
        }
    }
}

@Composable
private fun ItemList(
    items: List<Item>,
    onItemClick: (Item) -> Unit,
    onFavouriteToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier          = modifier.fillMaxSize(),
        contentPadding    = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items, key = { it.id }) { item ->
            AnimatedVisibility(
                visible = true,
                enter   = slideInVertically(tween(300)) { it / 2 } + fadeIn(tween(300)),
            ) {
                ItemCard(
                    item              = item,
                    onClick           = { onItemClick(item) },
                    onFavouriteToggle = { onFavouriteToggle(item.id) },
                )
            }
        }
    }
}

@Composable
private fun ItemCard(
    item: Item,
    onClick: () -> Unit,
    onFavouriteToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier  = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier            = Modifier.padding(16.dp),
            verticalAlignment   = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text     = item.title,
                    style    = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text     = item.description,
                    style    = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = onFavouriteToggle) {
                Icon(
                    imageVector        = if (item.isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (item.isFavourite) "Remove favourite" else "Add favourite",
                    tint               = if (item.isFavourite) MaterialTheme.colorScheme.primary
                                         else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(message: String, onDismiss: () -> Unit) {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(message, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun EmptyState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("No items found", style = MaterialTheme.typography.bodyLarge)
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    val previewItems = (1..5).map {
        Item("$it", "Item $it", "Description $it", "", it % 2 == 0)
    }
    MaterialTheme {
        HomeContent(
            state        = HomeState(items = previewItems),
            snackbarHost = SnackbarHostState(),
            onAction     = {},
        )
    }
}