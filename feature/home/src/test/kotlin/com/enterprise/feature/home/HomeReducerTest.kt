package com.enterprise.feature.home

import com.enterprise.core.testing.TestData
import com.enterprise.feature.home.mvi.HomeAction
import com.enterprise.feature.home.mvi.HomeReducer
import com.enterprise.feature.home.mvi.HomeState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pure reducer tests — no coroutines, no Android framework.
 *
 * These run on JVM only and are extremely fast (~ms per test).
 * Every state transition is tested in complete isolation.
 */
class HomeReducerTest {

    private val reducer = HomeReducer()
    private val initialState = HomeState()

    @Test
    fun `LoadItems sets isLoading true when items are empty`() {
        val result = reducer.reduce(initialState, HomeAction.LoadItems)
        assertTrue(result.isLoading)
    }

    @Test
    fun `LoadItems does NOT set isLoading when items already exist`() {
        val stateWithItems = initialState.copy(items = TestData.items(3))
        val result = reducer.reduce(stateWithItems, HomeAction.LoadItems)
        assertFalse(result.isLoading)
    }

    @Test
    fun `ItemsLoaded updates items and clears loading`() {
        val items  = TestData.items(5)
        val state  = initialState.copy(isLoading = true)
        val result = reducer.reduce(state, HomeAction.ItemsLoaded(items))

        assertEquals(5, result.items.size)
        assertFalse(result.isLoading)
        assertFalse(result.isRefreshing)
        assertNull(result.errorMessage)
    }

    @Test
    fun `ItemsLoadFailed sets errorMessage and clears loading`() {
        val state  = initialState.copy(isLoading = true)
        val result = reducer.reduce(state, HomeAction.ItemsLoadFailed("Timeout"))

        assertEquals("Timeout", result.errorMessage)
        assertFalse(result.isLoading)
    }

    @Test
    fun `RefreshItems sets isRefreshing without changing items`() {
        val items  = TestData.items(3)
        val state  = initialState.copy(items = items)
        val result = reducer.reduce(state, HomeAction.RefreshItems)

        assertTrue(result.isRefreshing)
        assertEquals(items, result.items)
    }

    @Test
    fun `ErrorDismissed clears errorMessage`() {
        val state  = initialState.copy(errorMessage = "Some error")
        val result = reducer.reduce(state, HomeAction.ErrorDismissed)
        assertNull(result.errorMessage)
    }

    @Test
    fun `FavouriteUpdated patches only the matching item`() {
        val items   = TestData.items(3)
        val updated = items[1].copy(isFavourite = true)
        val state   = initialState.copy(items = items)
        val result  = reducer.reduce(state, HomeAction.FavouriteUpdated(updated))

        assertFalse(result.items[0].isFavourite)
        assertTrue(result.items[1].isFavourite)
        assertFalse(result.items[2].isFavourite)
    }

    @Test
    fun `TabSelected updates selectedTabIndex`() {
        val result = reducer.reduce(initialState, HomeAction.TabSelected(1))
        assertEquals(1, result.selectedTabIndex)
    }

    @Test
    fun `derived state - favourites returns only favourite items`() {
        val items = listOf(
            TestData.item("1", isFavourite = true),
            TestData.item("2", isFavourite = false),
            TestData.item("3", isFavourite = true),
        )
        val state = initialState.copy(items = items)
        assertEquals(2, state.favourites.size)
    }

    @Test
    fun `derived state - isEmpty true when not loading and no items and no error`() {
        val state = HomeState(isLoading = false, items = emptyList(), errorMessage = null)
        assertTrue(state.isEmpty)
    }

    @Test
    fun `navigation actions do not mutate state`() {
        val item    = TestData.item()
        val before  = initialState.copy(items = listOf(item))

        // These actions are navigation-only; reducer should not change state
        val afterItemClick   = reducer.reduce(before, HomeAction.ItemClicked(item))
        val afterSearchClick = reducer.reduce(before, HomeAction.SearchClicked)
        val afterProfile     = reducer.reduce(before, HomeAction.ProfileClicked)

        assertEquals(before, afterItemClick)
        assertEquals(before, afterSearchClick)
        assertEquals(before, afterProfile)
    }
}