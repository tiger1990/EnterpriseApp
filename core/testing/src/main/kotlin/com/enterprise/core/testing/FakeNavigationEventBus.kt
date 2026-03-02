package com.enterprise.core.testing

import com.enterprise.core.navigation.NavigationEvent
import com.enterprise.core.navigation.NavigationEventBus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

// ─── Fake NavigationEventBus for unit tests ───────────────────────────────────

/**
 * Test double for NavigationEventBus.
 * Records all emitted events for assertion in tests.
 */
class FakeNavigationEventBus : NavigationEventBus() {
    val emittedEvents = mutableListOf<NavigationEvent>()

    override val navigationEvents: SharedFlow<NavigationEvent>
        get() = _flow.asSharedFlow()

    private val _flow = MutableSharedFlow<NavigationEvent>(replay = 1, extraBufferCapacity = 64)

    override suspend fun send(event: NavigationEvent) {
        emittedEvents.add(event)
        _flow.emit(event)
    }
}