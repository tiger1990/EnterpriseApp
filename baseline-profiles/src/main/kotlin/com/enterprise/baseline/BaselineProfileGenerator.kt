package com.enterprise.baseline

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Baseline Profile Generator.
 *
 * Generates startup and navigation traces used by the ART compiler to
 * AOT-compile hot methods, reducing startup time and jank.
 *
 * ─── How it works ─────────────────────────────────────────────────────────────
 * 1. Run this test on a physical device or emulator (API 28+, rooted optional)
 * 2. The test exercises the app's critical user journeys
 * 3. BaselineProfileRule captures which methods are executed
 * 4. A baseline-prof.txt is generated and bundled into the release APK
 * 5. On install, the system pre-compiles these methods
 *
 * ─── How to run ───────────────────────────────────────────────────────────────
 * ./gradlew :baseline-profiles:generateBaselineProfile
 * (or use Android Studio's "Generate Baseline Profile" action)
 *
 * ─── Performance impact ───────────────────────────────────────────────────────
 * Typically reduces cold start time by 20–40%.
 */
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() = rule.collect(
        packageName = "com.enterprise.app",
    ) {
        // Define critical user journeys (CUJs)
        // ── Critical journey 1: App cold start → Home screen ─────────────────
        pressHome()
        startActivityAndWait()
        waitForHomeScreenReady()

        // ── Critical journey 2: Home → Detail ────────────────────────────────
        device.findObject(By.text("Item Title 1"))?.click()
        // Wait for the Home screen element to disappear (indicating transition)
        device.wait(Until.gone(By.text("Discover")), 2_000)
        device.pressBack()
        // Wait for Home to be visible again before starting the next journey
        // waitForHomeScreenReady()

        // ── Critical journey 3: Home → Search ────────────────────────────────
        device.findObject(By.desc("Search"))?.click()
        device.wait(Until.hasObject(By.text("Search items...")), 2_000)
        device.pressBack()

        // ── Critical journey 4: Home → Profile ───────────────────────────────
        device.findObject(By.desc("Profile"))?.click()
        device.wait(Until.hasObject(By.text("Profile")), 2_000)
        device.pressBack()
    }

    private fun MacrobenchmarkScope.waitForHomeScreenReady() {
        device.wait(Until.hasObject(By.text("Discover")), 5_000)
    }
}