package com.enterprise.core.common.mvi

/**
 * ═══════════════════════════════════════════════════════════
 *  STRICT REDUCER-DRIVEN MVI CONTRACTS
 * ═══════════════════════════════════════════════════════════
 *
 *  Terminology:
 *   State  – Immutable snapshot of what the UI renders. ALWAYS a data class.
 *   Action – User intent or system event. Pure sealed class/interface.
 *   Effect  – One-shot side-effects (snackbar, navigation trigger).
 *             NOT part of state; consumed exactly once.
 *   Reducer – (State, Action) -> State. Pure function, no coroutines, no DI.
 *             Unit-testable without Android framework.
 *   Middleware – Handles async work triggered by an Action.
 *             Emits new Actions back to the store when async work completes.
 *
 *  Flow:
 *    UI → dispatch(Action) → Reducer → new State → UI re-renders
 *                                    ↘ Middleware (async) → dispatch(Action) ↗
 *                                    ↘ NavigationEvent → NavigationEventBus
 */

/** Marker interface for all UI states. */
interface UiState

/** Marker interface for all user/system actions. */
interface UiAction

/** Marker interface for one-shot side effects consumed by the UI. */
interface UiEffect

/**
 * Pure reducer function type.
 * Must be a pure function: same inputs → same output, no side effects.
 */
fun interface Reducer<S : UiState, A : UiAction> {
    fun reduce(state: S, action: A): S
}

/**
 * Concurrency policy for an action's async middleware work.
 *
 * The reducer always runs immediately and unconditionally — this policy only
 * governs whether the [MviViewModel.handleAction] coroutine is launched,
 * dropped, or replaced.
 *
 * ─── Choosing a policy ───────────────────────────────────────────────────────
 *
 *  [Concurrent]        — Default. Every dispatch gets its own coroutine.
 *                        Use for: fast navigation events, state-only actions.
 *
 *  [DropIfBusy]        — If middleware tagged with key is already running,
 *                        the new dispatch is silently skipped (reducer ran;
 *                        async side-effect is not re-triggered).
 *                        Use for: idempotent loads (LoadItem, LoadProfile) and
 *                        mutations that should not stack (ToggleFavourite).
 *
 *  [CancelAndRelaunch] — Cancel the in-flight coroutine for key, then start
 *                        fresh. Use for: user-driven queries where only the
 *                        latest result matters (Search, Filter).
 */
sealed interface ActionConcurrency {
    data object Concurrent : ActionConcurrency
    data class DropIfBusy(val key: String) : ActionConcurrency
    data class CancelAndRelaunch(val key: String) : ActionConcurrency
}