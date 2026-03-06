package com.enterprise.app.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

/**
 * Activity-level DI module.
 *
 * NOTE: NavigationEventBus and NavigationMiddleware bindings have been moved to
 * NavigationModule in the :core:navigation module to avoid duplicate bindings.
 */
@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ActivityModule {
    // Add app-specific activity-retained bindings here
}
