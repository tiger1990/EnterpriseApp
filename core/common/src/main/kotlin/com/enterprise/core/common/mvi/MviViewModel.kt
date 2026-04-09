package com.enterprise.core.common.mvi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enterprise.core.navigation.NavigationEvent
import com.enterprise.core.navigation.NavigationEventBus
import kotlinx.coroutines.Job
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
 * Subclasses receive [savedStateHandle] and call [saveTo] /
 * [getOrDefault] to persist critical state across process death.
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
    //
    // restorePersistedState() is called here — before the StateFlow is created —
    // so that process-death restoration is applied atomically to the initial
    // state rather than as a second mutation in init{}.
    private val _state = MutableStateFlow(restorePersistedState(initialState))
    val state: StateFlow<S> = _state.asStateFlow()

    // ── Effects (one-shot) ────────────────────────────────────────────────────
    private val _effects = Channel<E>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    // ── Middleware job registry ────────────────────────────────────────────────
    // Keyed by the string returned from actionConcurrency(). Access is safe on
    // the main thread — dispatch() is always called from the UI thread and
    // viewModelScope uses Dispatchers.Main.immediate.
    private val middlewareJobs = mutableMapOf<String, Job>()

    // ─── Persistence hooks ────────────────────────────────────────────────────

    /**
     * Override to restore lightweight fields from [savedStateHandle] onto the
     * initial state after process death. Called once, before the [StateFlow] is
     * created, so the restored state is the *first* value collectors see.
     *
     * Only restore fields that cannot be re-derived cheaply from the repository.
     * IDs that come from Nav3 route keys do NOT need to be handled here.
     *
     * Example:
     * ```
     * override fun restorePersistedState(initial: MyState) = initial.copy(
     *     scrollIndex = savedStateHandle.getOrDefault(KEY_SCROLL, 0),
     *     activeTab   = savedStateHandle.getOrDefault(KEY_TAB, 0),
     * )
     * ```
     */
    protected open fun restorePersistedState(initial: S): S = initial

    /**
     * Override to write lightweight fields to [savedStateHandle] whenever state
     * changes. Called synchronously after every [reduce] inside [dispatch], so
     * it captures state even if the subsequent async [handleAction] is killed
     * mid-flight (e.g. during an optimistic update).
     *
     * Only persist what is needed to restore the screen — scroll position, tab
     * index, query strings. Do NOT persist full lists or domain objects.
     *
     * Example:
     * ```
     * override fun persistState(state: MyState) {
     *     savedStateHandle.saveTo(KEY_SCROLL, state.scrollIndex)
     *     savedStateHandle.saveTo(KEY_TAB,    state.activeTab)
     * }
     * ```
     */
    protected open fun persistState(state: S) {}

    // ─── Concurrency policy ───────────────────────────────────────────────────

    /**
     * Override to declare the [ActionConcurrency] policy for actions that trigger
     * async middleware. Unhandled actions default to [ActionConcurrency.Concurrent].
     *
     * The reducer always runs regardless of this policy. The policy only controls
     * whether [handleAction] is launched, dropped, or replaced.
     *
     * ```
     * override fun actionConcurrency(action: MyAction) = when (action) {
     *     MyAction.Load             -> ActionConcurrency.DropIfBusy("load")
     *     is MyAction.Search        -> ActionConcurrency.CancelAndRelaunch("search")
     *     MyAction.FavouriteToggled -> ActionConcurrency.DropIfBusy("favourite")
     *     else                      -> ActionConcurrency.Concurrent
     * }
     * ```
     */
    protected open fun actionConcurrency(action: A): ActionConcurrency = ActionConcurrency.Concurrent

    // ─── Public API ───────────────────────────────────────────────────────────

    /**
     * Entry point for all user-initiated and system-initiated actions.
     *
     * Order of operations:
     * 1. Pure reducer computes new state synchronously.
     * 2. [persistState] writes lightweight fields to SavedStateHandle synchronously.
     * 3. [handleAction] is launched (or dropped/replaced) per [actionConcurrency].
     */
    fun dispatch(action: A) {
        _state.update { currentState -> reducer.reduce(currentState, action) }
        persistState(_state.value)
        when (val policy = actionConcurrency(action)) {
            ActionConcurrency.Concurrent -> {
                viewModelScope.launch { handleAction(action) }
            }
            is ActionConcurrency.DropIfBusy -> {
                if (middlewareJobs[policy.key]?.isActive != true) {
                    middlewareJobs[policy.key] = viewModelScope.launch { handleAction(action) }
                }
            }
            is ActionConcurrency.CancelAndRelaunch -> {
                middlewareJobs[policy.key]?.cancel()
                middlewareJobs[policy.key] = viewModelScope.launch { handleAction(action) }
            }
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
}

// ─── Process Death ────────────────────────────────────────────────────────

/**
 * Save lightweight state to SavedStateHandle for process-death restoration.
 * Called by subclasses; typically in init{} after restoring state.
 *
 * Only save what's needed to restore the screen — IDs, queries, scroll
 * position. Do NOT save full lists (re-fetch from repository instead).
 */
fun <T> SavedStateHandle.saveTo(key: String, value: T) {
    this[key] = value
}

/**
 * Convenience to get a value from SavedStateHandle or use a default.
 * removed protected
 */
fun <T> SavedStateHandle.getOrDefault(key: String, default: T): T =
    get<T>(key) ?: default
// ─── Process Death ────────────────────────────────────────────────────────
