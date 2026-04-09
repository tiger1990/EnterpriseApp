package com.enterprise.feature.home.mvi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.enterprise.core.common.mvi.ActionConcurrency
import com.enterprise.core.common.mvi.MviViewModel
import com.enterprise.core.common.mvi.getOrDefault
import com.enterprise.core.common.mvi.saveTo
import com.enterprise.core.common.result.Result
import com.enterprise.core.domain.usecase.ObserveItemsUseCase
import com.enterprise.core.navigation.NavigationEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * HomeViewModel — state coordinator for the Home feature.
 *
 * ─── Responsibilities ────────────────────────────────────────────────────────
 *  ✅ Owns HomeState lifecycle (Reducer + SavedStateHandle)
 *  ✅ Declares ActionConcurrency policy per action
 *  ✅ Observes the items stream (needs viewModelScope lifetime)
 *  ✅ Delegates all side-effect work to [HomeActionHandler]
 *
 *  ❌ Does NOT perform network calls
 *  ❌ Does NOT know about navigation routes
 *  ❌ Does NOT produce snackbar strings
 *
 * ─── Why stream observation stays here ───────────────────────────────────────
 * observeItems() emits continuously for the ViewModel's entire lifetime.
 * That lifecycle bond belongs in the ViewModel, not in a stateless handler.
 * The handler only processes discrete, single-shot actions.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val observeItems: ObserveItemsUseCase,
    private val actionHandler: HomeActionHandler,
    navigationBus: NavigationEventBus,
    savedStateHandle: SavedStateHandle,
) : MviViewModel<HomeState, HomeAction, HomeEffect>(
    initialState     = HomeState(),
    reducer          = HomeReducer(),
    navigationBus    = navigationBus,
    savedStateHandle = savedStateHandle,
) {

    // ─── Concurrency policy ───────────────────────────────────────────────────
    // Declared here because it is a state-coordination concern: which actions
    // may run in parallel, which cancel each other, which are idempotent.
    // The handler never needs to know about this — it just does the work.
    override fun actionConcurrency(action: HomeAction) = when (action) {
        HomeAction.RefreshItems        -> ActionConcurrency.CancelAndRelaunch("refresh")
        is HomeAction.FavouriteToggled -> ActionConcurrency.DropIfBusy("favourite_${action.itemId}")
        is HomeAction.ItemClicked      -> ActionConcurrency.DropIfBusy("nav_item")
        HomeAction.SearchClicked       -> ActionConcurrency.DropIfBusy("nav_search")
        HomeAction.ProfileClicked      -> ActionConcurrency.DropIfBusy("nav_profile")
        else                           -> ActionConcurrency.Concurrent
    }

    // ─── Process death ────────────────────────────────────────────────────────
    override fun restorePersistedState(initial: HomeState): HomeState =
        initial.copy(selectedTabIndex = savedStateHandle.getOrDefault(KEY_TAB, 0))

    override fun persistState(state: HomeState) {
        savedStateHandle.saveTo(KEY_TAB, state.selectedTabIndex)
    }

    init {
        dispatch(HomeAction.LoadItems)
        observeItemsStream()
    }

    // ─── Side-effect delegation ───────────────────────────────────────────────
    override suspend fun handleAction(action: HomeAction) {
        actionHandler.handle(
            action     = action,
            dispatch   = ::dispatch,
            emitEffect = { emitEffect(it) },
        )
    }

    // ─── Continuous stream observation ────────────────────────────────────────
    // Stays here rather than in HomeActionHandler because it is tied to
    // viewModelScope — the stream must live and die with the ViewModel.
    private fun observeItemsStream() {
        observeItems(Unit).onEach { result ->
            when (result) {
                is Result.Success -> dispatch(HomeAction.ItemsLoaded(result.data.map { it.toHomeUiModel() }))
                is Result.Error   -> dispatch(HomeAction.ItemsLoadFailed(result.exception.message ?: "Unknown error"))
                Result.Loading    -> Unit
            }
        }.launchIn(viewModelScope)
    }

    companion object {
        private const val KEY_TAB = "home_selected_tab"
    }
}
