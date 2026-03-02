package com.enterprise.app.di

import com.enterprise.core.navigation.NavigationEventBus
import com.enterprise.core.navigation.NavigationMiddleware
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

/**
 * Activity-level DI module.
 *
 * NavigationEventBus is @ActivityRetainedScoped:
 *  ✅ Survives rotation (ViewModel lifecycle)
 *  ✅ Destroyed when Activity is finished (no leaks)
 *  ✅ One instance per Activity — correct for single-Activity apps
 *  ❌ NOT a Singleton — avoids cross-Activity contamination
 */
@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class `ActivityModule.kt` {

    @Binds
    @ActivityRetainedScoped
    abstract fun bindNavigationMiddleware(bus: NavigationEventBus): NavigationMiddleware

    companion object {
        @Provides
        @ActivityRetainedScoped
        fun provideNavigationEventBus(): NavigationEventBus = NavigationEventBus()
    }
}