package com.enterprise.feature.home.mvi

import com.enterprise.core.common.result.Result
import com.enterprise.core.domain.usecase.RefreshItemsUseCase
import com.enterprise.core.domain.usecase.ToggleFavouriteUseCase
import com.enterprise.core.navigation.DetailRoute
import com.enterprise.core.navigation.NavigationEvent
import com.enterprise.core.navigation.NavigationEventBus
import com.enterprise.core.navigation.ProfileRoute
import com.enterprise.core.navigation.SearchRoute
import javax.inject.Inject

/**
 * HomeActionHandler — owns all async side-effect work for the Home feature.
 *
 * ─── Responsibility split ────────────────────────────────────────────────────
 *  HomeViewModel      → State coordination: Reducer, SavedState, stream observation,
 *                       ActionConcurrency policy.
 *  HomeActionHandler  → Side effects: network calls, navigation, snackbar effects.
 *
 * ─── Why not a generic Middleware<A> interface ────────────────────────────────
 * A generic interface would require the return type to unify Actions AND Effects
 * AND NavigationEvents into a single flow — fighting Kotlin's type system.
 * Being typed to HomeAction/HomeEffect means exhaustive when-branches,
 * zero casts, and the compiler catches any unhandled action at build time.
 *
 * ─── NavigationEventBus injected directly ────────────────────────────────────
 * Navigation is a side effect like any other. The handler owns it rather than
 * delegating back to the ViewModel via a callback, which would create a circular
 * dependency between the two classes.
 *
 * ─── dispatch / emitEffect passed at call time ───────────────────────────────
 * Keeping this class stateless (no lateinit, no stored lambdas) makes it safe
 * to test by calling handle() directly with a capturing lambda — no ViewModel,
 * no StateFlow, no coroutine test harness required.
 *
 * ─── Stream observation stays in HomeViewModel ───────────────────────────────
 * observeItems() emits continuously and needs viewModelScope to survive the
 * ViewModel lifetime. That lifecycle relationship belongs in the ViewModel, not
 * in a stateless handler.
 *
 * ─── Adding a new action ─────────────────────────────────────────────────────
 * 1. Add the action variant to HomeAction (HomeMvi.kt)
 * 2. Add the state reduction to HomeReducer (HomeMvi.kt)
 * 3. Add the side-effect handler here — one when-branch, injected dependency
 * 4. Add the concurrency policy in HomeViewModel.actionConcurrency() if needed
 *
 * ─── Testing ─────────────────────────────────────────────────────────────────
 * ```
 * val dispatched = mutableListOf<HomeAction>()
 * val handler = HomeActionHandler(fakeRefresh, fakeToggle, fakeNavBus)
 * handler.handle(HomeAction.FavouriteToggled("item_1"), dispatched::add) { }
 * assertThat(dispatched).containsExactly(HomeAction.FavouriteUpdated(...))
 * ```
 */
class HomeActionHandler @Inject constructor(
    private val refreshItems: RefreshItemsUseCase,
    private val toggleFavourite: ToggleFavouriteUseCase,
    private val navigationBus: NavigationEventBus,
) {

    suspend fun handle(
        action: HomeAction,
        dispatch: (HomeAction) -> Unit,
        emitEffect: suspend (HomeEffect) -> Unit,
    ) {
        when (action) {

            // ── Data actions ──────────────────────────────────────────────────

            HomeAction.LoadItems -> Unit
            // observeItemsStream() in HomeViewModel handles continuous updates.
            // Nothing async to do here — the stream covers initial load and refresh.

            HomeAction.RefreshItems -> {
                when (refreshItems()) {
                    is Result.Error -> emitEffect(HomeEffect.ShowSnackbar("Refresh failed"))
                    else -> Unit
                    // Success: observeItemsStream() picks up the new data automatically.
                    // isRefreshing is reset when ItemsLoaded arrives from the stream.
                }
            }

            is HomeAction.FavouriteToggled -> {
                when (val result = toggleFavourite(action.itemId)) {
                    is Result.Success -> dispatch(HomeAction.FavouriteUpdated(result.data.toHomeUiModel()))
                    is Result.Error   -> emitEffect(HomeEffect.ShowSnackbar("Failed to update favourite"))
                    else -> Unit
                }
            }

            // ── Navigation actions ────────────────────────────────────────────

            is HomeAction.ItemClicked -> navigationBus.send(
                NavigationEvent.NavigateTo(DetailRoute(action.itemId, action.itemTitle))
            )

            HomeAction.SearchClicked  -> navigationBus.send(NavigationEvent.NavigateTo(SearchRoute))

            HomeAction.ProfileClicked -> navigationBus.send(NavigationEvent.NavigateTo(ProfileRoute))

            // ── State-only actions — fully handled by the Reducer ─────────────
            // Listed explicitly so the compiler enforces exhaustiveness.
            // Any new action added to HomeAction will cause a compile error here
            // until it is consciously placed in the right bucket.

            is HomeAction.TabSelected,
            is HomeAction.ItemsLoaded,
            is HomeAction.ItemsLoadFailed,
            is HomeAction.FavouriteUpdated,
            HomeAction.ErrorDismissed -> Unit
        }
    }
}
