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