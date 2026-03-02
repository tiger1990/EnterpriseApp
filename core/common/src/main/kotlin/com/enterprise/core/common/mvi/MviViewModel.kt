package com.enterprise.core.common.mvi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enterprise.core.navigation.NavigationEvent
import com.enterprise.core.navigation.NavigationEventBus
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base ViewModel enforcing strict MVI.
 *
 * ─── Process Death Restoration ───────────────────────────────────────────────
 * Subclasses receive [savedStateHandle] and call [saveStateToHandle] /
 * [restoreStateFromHandle] to persist critical state across process death.
 * Only lightweight, parcelable/serializable fields should be saved.
 *
 * ─── Navigation ──────────────────────────────────────────────────────────────
 * ViewModels NEVER hold NavController.
 * Use [navigate] to emit a NavigationEvent into the bus.
 * The AppNavHost (in :app) is the sole collector.
 *
 * ─── Reducer guarantee ───────────────────────────────────────────────────────
 * All state mutations go through [reduce].
 * No direct assignments to _state are permitted from subclasses.
 *
 * @param S         UiState type
 * @param A         UiAction type
 * @param E         UiEffect type
 * @param initialState  Initial state (may be overridden by saved state restoration)
 * @param reducer       Pure (State, Action) -> State function
 * @param navigationBus Hilt-injected event bus, scoped to ActivityRetained
 * @param savedStateHandle Provided by Hilt automatically
 */
abstract class MviViewModel<S : UiState, A : UiAction, E : UiEffect>(
    initialState: S,
    private val reducer: Reducer<S, A>,
    protected val navigationBus: NavigationEventBus,
    protected val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // ── State ─────────────────────────────────────────────────────────────────
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    // ── Effects (one-shot) ────────────────────────────────────────────────────
    private val _effects = Channel<E>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    // ─── Public API ───────────────────────────────────────────────────────────

    /**
     * Entry point for all user-initiated and system-initiated actions.
     * Calls the pure reducer synchronously, then runs middleware asynchronously.
     */
    fun dispatch(action: A) {
        // 1. Synchronously compute new state via pure reducer
        _state.update { currentState -> reducer.reduce(currentState, action) }

        // 2. Handle side-effects asynchronously (network, navigation, etc.)
        viewModelScope.launch {
            handleAction(action)
        }
    }

    /**
     * Middleware: override to handle async work triggered by actions.
     * Call [dispatch] again when async work produces a result action.
     * Call [emitEffect] for one-shot UI effects.
     * Call [navigate] for navigation.
     */
    protected open suspend fun handleAction(action: A) {}

    /** Emit a one-shot effect to the UI (e.g., show snackbar). */
    protected suspend fun emitEffect(effect: E) {
        _effects.send(effect)
    }

    /** Emit a navigation event. NEVER pass NavController here. */
    protected suspend fun navigate(event: NavigationEvent) {
        navigationBus.send(event)
    }

    // ─── Process Death ────────────────────────────────────────────────────────

    /**
     * Save lightweight state to SavedStateHandle for process-death restoration.
     * Called by subclasses; typically in init{} after restoring state.
     *
     * Only save what's needed to restore the screen — IDs, queries, scroll
     * position. Do NOT save full lists (re-fetch from repository instead).
     */
    protected fun <T> SavedStateHandle.saveTo(key: String, value: T) {
        this[key] = value
    }

    /**
     * Convenience to get a value from SavedStateHandle or use a default.
     */
    protected fun <T> SavedStateHandle.getOrDefault(key: String, default: T): T =
        get<T>(key) ?: default
}