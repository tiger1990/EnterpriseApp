package com.enterprise.core.navigation.di

import com.enterprise.core.navigation.NavigationEventBus
import com.enterprise.core.navigation.NavigationMiddleware
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

/**
 * Navigation module scoped to ActivityRetained.
 *
 * ─── Why ActivityRetainedScoped? ─────────────────────────────────────────────
 * - Survives configuration changes (rotation) — NavController is re-created
 *   but the event bus retains buffered events.
 * - Destroyed on process death — correct, because on restoration the
 *   SavedStateHandle recreates the back-stack and we start fresh.
 * - NOT a Singleton — avoids cross-Activity leaks in multi-Activity apps.
 *
 * NavigationEventBus is @ActivityRetainedScoped:
 *  ✅ Survives rotation (ViewModel lifecycle)
 *  ✅ Destroyed when Activity is finished (no leaks)
 *  ✅ One instance per Activity — correct for single-Activity apps
 *  ❌ NOT a Singleton — avoids cross-Activity contamination
 *
 * Each Activity (and its ViewModels) get their own NavigationEventBus instance.
 */
@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class NavigationModule {

    @Binds
    @ActivityRetainedScoped
    abstract fun bindNavigationMiddleware(bus: NavigationEventBus): NavigationMiddleware

    companion object {
        @Provides
        @ActivityRetainedScoped
        fun provideNavigationEventBus(): NavigationEventBus = NavigationEventBus()
    }
}
