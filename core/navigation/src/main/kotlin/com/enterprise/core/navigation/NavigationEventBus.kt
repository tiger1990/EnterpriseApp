package com.enterprise.core.navigation

import androidx.compose.runtime.Stable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Concrete NavigationMiddleware implementation.
 *
 * ─── Why Channel instead of SharedFlow(replay=1) ─────────────────────────────
 * SharedFlow(replay=1) caches the last event and re-delivers it to every new
 * collector. In AppNavHost, LaunchedEffect(backStack) restarts whenever
 * backStack changes identity (which happens after a configuration change that
 * recreates the SnapshotStateList). This caused the replayed event to trigger
 * a second navigation, pushing a duplicate destination or double-popping.
 *
 * Channel(BUFFERED) provides exactly-once delivery:
 *  - Events emitted before the collector attaches are buffered (handles the
 *    ViewModel-init-before-NavHost-composition gap that replay=1 was solving).
 *  - A consumed event is gone — a restarted LaunchedEffect reads forward from
 *    the buffer, never re-processing an already-handled command.
 *  - receiveAsFlow() supports collector restart without closing the channel.
 *
 * ─── Scoping ─────────────────────────────────────────────────────────────────
 *  - @ActivityRetainedScoped: outlives configuration changes, not process death.
 *  - The Channel instance is retained across config changes; any buffered but
 *    undelivered events remain queued for the restarted collector.
 *
 * ─── @Stable ─────────────────────────────────────────────────────────────────
 * Marked @Stable so Compose treats it as a stable parameter in composables like
 * EnterpriseApp. The contract holds: the same @ActivityRetainedScoped instance
 * is passed for the entire Activity lifetime, and Compose never observes the
 * internal Channel — state changes inside the bus are invisible to the
 * composition system and do not trigger recomposition.
 */
@Stable
open class NavigationEventBus : NavigationMiddleware {

    private val _channel = Channel<NavigationEvent>(Channel.BUFFERED)

    override val navigationEvents: Flow<NavigationEvent>
        get() = _channel.receiveAsFlow()

    /** Called by ViewModels inside the MVI action/reducer pipeline. */
    open suspend fun send(event: NavigationEvent) {
        _channel.send(event)
    }
}
