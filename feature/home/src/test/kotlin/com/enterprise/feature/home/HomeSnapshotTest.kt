package com.enterprise.feature.home

import androidx.compose.material3.SnackbarHostState
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.enterprise.core.testing.TestData
import com.enterprise.feature.home.mvi.HomeState
import com.enterprise.feature.home.ui.HomeContent
import org.junit.Rule
import org.junit.Test

/**
 * Paparazzi snapshot tests for HomeContent.
 *
 * These tests render composables to a bitmap and compare against golden images.
 * They run on JVM (no emulator needed) and catch unintended visual regressions.
 *
 * Run: ./gradlew :feature:home:recordPaparazziDebug    (create golden images)
 * Run: ./gradlew :feature:home:verifyPaparazziDebug    (verify no regression)
 *
 * Golden images are committed to source control.
 */
class HomeSnapshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_6,
        theme        = "android:Theme.Material.Light.NoActionBar",
    )

    @Test
    fun `snapshot - loading state`() {
        paparazzi.snapshot {
            HomeContent(
                state        = HomeState(isLoading = true),
                snackbarHost = SnackbarHostState(),
                onAction     = {},
            )
        }
    }

    @Test
    fun `snapshot - items loaded state`() {
        val items = TestData.items(5)
        paparazzi.snapshot {
            HomeContent(
                state        = HomeState(items = items),
                snackbarHost = SnackbarHostState(),
                onAction     = {},
            )
        }
    }

    @Test
    fun `snapshot - empty state`() {
        paparazzi.snapshot {
            HomeContent(
                state        = HomeState(isLoading = false, items = emptyList()),
                snackbarHost = SnackbarHostState(),
                onAction     = {},
            )
        }
    }

    @Test
    fun `snapshot - error state`() {
        paparazzi.snapshot {
            HomeContent(
                state        = HomeState(errorMessage = "Failed to load items. Please try again."),
                snackbarHost = SnackbarHostState(),
                onAction     = {},
            )
        }
    }

    @Test
    fun `snapshot - favourites tab selected`() {
        val items = TestData.items(5).mapIndexed { i, item -> item.copy(isFavourite = i % 2 == 0) }
        paparazzi.snapshot {
            HomeContent(
                state        = HomeState(items = items, selectedTabIndex = 1),
                snackbarHost = SnackbarHostState(),
                onAction     = {},
            )
        }
    }

    @Test
    fun `snapshot - refreshing state`() {
        val items = TestData.items(3)
        paparazzi.snapshot {
            HomeContent(
                state        = HomeState(items = items, isRefreshing = true),
                snackbarHost = SnackbarHostState(),
                onAction     = {},
            )
        }
    }
}