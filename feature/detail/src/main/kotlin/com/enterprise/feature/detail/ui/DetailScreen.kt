package com.enterprise.feature.detail.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.enterprise.core.tokens.R
import com.enterprise.feature.detail.mvi.DetailAction
import com.enterprise.feature.detail.mvi.DetailEffect
import com.enterprise.feature.detail.mvi.DetailState
import com.enterprise.feature.detail.mvi.DetailViewModel

// ─── Screen entry point ───────────────────────────────────────────────────────

@Composable
fun DetailScreen(
    viewModel: DetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is DetailEffect.ShowSnackbar -> snackBarHostState.showSnackbar(effect.message)
            }
        }
    }

    DetailContent(
        state = state,
        snackBarHost = snackBarHostState,
        onAction = viewModel::dispatch,
    )
}

// ─── Stateless content ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DetailContent(
    state: DetailState,
    snackBarHost: SnackbarHostState,
    onAction: (DetailAction) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Show title immediately from nav arg — no loading flicker
                    Text(
                        text = state.item?.title ?: state.itemTitle,
                        maxLines = 1,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onAction(DetailAction.BackPressed) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    state.item?.let { item ->
                        IconButton(onClick = { onAction(DetailAction.FavouriteToggled) }) {
                            Icon(
                                painter = painterResource(id = if (item.isFavourite) R.drawable.ic_favorite else R.drawable.ic_heart_broken),
                                contentDescription = "Toggle favourite",
                                tint = if (item.isFavourite) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackBarHost) },
    ) { paddingValues ->

        AnimatedContent(
            targetState = state,
            label = "detail_content",
            modifier = Modifier.padding(paddingValues),
        ) { s ->
            when {
                s.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }

                s.errorMessage != null -> Box(
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    Alignment.Center,
                ) {
                    Text(s.errorMessage)
                }

                s.item != null -> DetailBody(item = s.item)
            }
        }
    }
}

@Composable
private fun DetailBody(
    item: com.enterprise.core.domain.model.Item,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically(tween(400)) { -it / 3 } + fadeIn(tween(400)),
        ) {
            Column {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.headlineMedium,
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Item ID: ${item.id}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                )
            }
        }
    }
}