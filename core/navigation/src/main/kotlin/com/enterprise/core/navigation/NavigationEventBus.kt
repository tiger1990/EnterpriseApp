package com.enterprise.core.navigation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Concrete NavigationMiddleware implementation.
 *
 * Uses a SharedFlow with replay=1 so that:
 *  - Navigation events survive the gap between ViewModel init and NavHost composition.
 *  - The "last intended" destination is always available to the collector.
 *
 * Scoping rules:
 *  - This is injected via Hilt with @ActivityRetainedScoped so it outlives
 *    configuration changes but NOT process death.
 *  - On process death the back-stack is restored by SavedStateHandle (see
 *    individual ViewModels).
 */
open class NavigationEventBus : NavigationMiddleware {

    private val _mutableFlow = MutableSharedFlow<NavigationEvent>(
        replay = 1,
        extraBufferCapacity = 4,
    )

    override val navigationEvents: SharedFlow<NavigationEvent>
        get() = _mutableFlow.asSharedFlow()

    /** Called by ViewModels inside the MVI action/reducer pipeline. */
    open suspend fun send(event: NavigationEvent) {
        _mutableFlow.emit(event)
    }
}
