package com.enterprise.core.navigation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Concrete NavigationMiddleware implementation.
 *
 * Uses a Channel (capacity = UNLIMITED) converted to a Flow so that:
 *  - Navigation events are NOT broadcast to multiple collectors (Channel semantics)
 *  - Exactly one NavHost processes each event
 *  - Events are buffered while the NavHost is not ready (e.g. during recomposition)
 *
 * Scoping rules:
 *  - This is injected via Hilt with @ActivityRetainedScoped so it outlives
 *    configuration changes but NOT process death.
 *  - On process death the back-stack is restored by SavedStateHandle (see
 *    individual ViewModels) and navigation events are re-emitted as needed.
 */
class NavigationEventBus : NavigationMiddleware {

    private val _channel = Channel<NavigationEvent>(Channel.UNLIMITED)

    override val navigationEvents: SharedFlow<NavigationEvent>
        // Convert to SharedFlow with replay=0 — each event consumed once
        get() = _mutableFlow.asSharedFlow()

    private val _mutableFlow = MutableSharedFlow<NavigationEvent>(
        extraBufferCapacity = 64,
    )

    /** Called by ViewModels inside the MVI action/reducer pipeline. */
    suspend fun send(event: NavigationEvent) {
        _mutableFlow.emit(event)
    }
}