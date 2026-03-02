package com.enterprise.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class.
 *
 * @HiltAndroidApp triggers Hilt's code generation and sets up the
 * application-level component (SingletonComponent).
 *
 * All singleton-scoped dependencies (repositories, network, etc.) are
 * initialized lazily by Hilt when first requested.
 */
@HiltAndroidApp
class EnterpriseApplication : Application()