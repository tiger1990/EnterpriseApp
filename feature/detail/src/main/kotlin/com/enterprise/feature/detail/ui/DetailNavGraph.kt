package com.enterprise.feature.detail.ui

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.enterprise.core.navigation.DetailRoute
import com.enterprise.core.navigation.EntryBuilder
import com.enterprise.feature.detail.mvi.DetailViewModel

/**
 * Registers the Detail feature's destination.
 *
 * The entry<K> lambda receives the typed [DetailRoute] key directly from Nav3.
 * Route args are passed to [DetailViewModel] via its @AssistedFactory — the
 * Nav3-idiomatic pattern that replaces the Nav2 savedStateHandle.toRoute() call.
 *
 * Process death: back stack is restored by rememberNavBackStack → entry lambda
 * fires with the correct key → fresh VM is created with correct args.
 *
 * Deep link (enterprise://detail/{itemId}/{itemTitle}) — see DetailRoute.DEEP_LINK_URI.
 * Handled in DeepLinkRouter / MainActivity.
 */
fun EntryBuilder.detailEntries() {
    scope.entry<DetailRoute> { key ->
        val viewModel: DetailViewModel = hiltViewModel<DetailViewModel, DetailViewModel.Factory>(
            creationCallback = { factory -> factory.create(key.itemId, key.itemTitle) },
        )
        DetailScreen(viewModel = viewModel)
    }
}
