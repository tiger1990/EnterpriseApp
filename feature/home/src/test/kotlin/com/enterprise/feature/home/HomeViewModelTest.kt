package com.enterprise.feature.home

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.enterprise.core.common.result.Result
import com.enterprise.core.domain.usecase.ObserveItemsUseCase
import com.enterprise.core.domain.usecase.ToggleFavouriteUseCase
import com.enterprise.core.navigation.DetailRoute
import com.enterprise.core.navigation.NavigationEvent
import com.enterprise.core.navigation.SearchRoute
import com.enterprise.core.testing.FakeItemRepository
import com.enterprise.core.testing.FakeNavigationEventBus
import com.enterprise.core.testing.TestData
import com.enterprise.feature.home.mvi.HomeAction
import com.enterprise.feature.home.mvi.HomeEffect
import com.enterprise.feature.home.mvi.HomeViewModel
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for HomeViewModel.
 *
 * Tests:
 *  1. Pure reducer — no coroutines, pure input → output verification
 *  2. Middleware side effects — navigation, async data loading
 *  3. Process death restoration — SavedStateHandle key persistence
 *  4. Effect emission — snackbar on toggle failure
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeItemRepository
    private lateinit var fakeNavigationBus: FakeNavigationEventBus
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository      = FakeItemRepository()
        fakeNavigationBus   = FakeNavigationEventBus()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(savedState: Map<String, Any> = emptyMap()): HomeViewModel {
        val savedStateHandle = SavedStateHandle(savedState)
        return HomeViewModel(
            observeItems     = ObserveItemsUseCase(fakeRepository),
            toggleFavourite  = ToggleFavouriteUseCase(fakeRepository),
            navigationBus    = fakeNavigationBus,
            savedStateHandle = savedStateHandle,
        )
    }

    // ─── Reducer tests ────────────────────────────────────────────────────────

    @Test
    fun `reducer - ItemsLoaded updates state correctly`() = runTest {
        val items     = TestData.items(3)
        fakeRepository.setItems(items)
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(3, state.items.size)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
    }

    @Test
    fun `reducer - ErrorDismissed clears error message`() = runTest {
        fakeRepository.setError("Network error")
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.dispatch(HomeAction.ErrorDismissed)

        assertNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun `reducer - TabSelected updates selectedTabIndex`() = runTest {
        viewModel = createViewModel()

        viewModel.dispatch(HomeAction.TabSelected(1))

        assertEquals(1, viewModel.state.value.selectedTabIndex)
    }

    @Test
    fun `reducer - FavouriteUpdated patches item in list`() = runTest {
        val items = TestData.items(3)
        fakeRepository.setItems(items)
        val updatedItem = items[0].copy(isFavourite = true)
        fakeRepository.toggleFavouriteResult = Result.Success(updatedItem)
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.dispatch(HomeAction.FavouriteToggled(items[0].id))
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value.items.first().isFavourite)
    }

    // ─── Navigation middleware tests ──────────────────────────────────────────

    @Test
    fun `middleware - ItemClicked emits NavigateTo DetailRoute`() = runTest {
        val item  = TestData.item()
        viewModel = createViewModel()

        viewModel.dispatch(HomeAction.ItemClicked(item))
        testDispatcher.scheduler.advanceUntilIdle()

        val event = fakeNavigationBus.emittedEvents.last()
        assertTrue(event is NavigationEvent.NavigateTo)
        val route = (event as NavigationEvent.NavigateTo).route
        assertTrue(route is DetailRoute)
        assertEquals(item.id, (route as DetailRoute).itemId)
    }

    @Test
    fun `middleware - SearchClicked emits NavigateTo SearchRoute`() = runTest {
        viewModel = createViewModel()

        viewModel.dispatch(HomeAction.SearchClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        val event = fakeNavigationBus.emittedEvents.last()
        assertTrue(event is NavigationEvent.NavigateTo)
        assertEquals(SearchRoute, (event as NavigationEvent.NavigateTo).route)
    }

    // ─── Process death tests ──────────────────────────────────────────────────

    @Test
    fun `process death - restores selectedTabIndex from SavedStateHandle`() = runTest {
        // Simulate process death: recreate VM with saved state
        viewModel = createViewModel(savedState = mapOf("home_selected_tab" to 1))

        assertEquals(1, viewModel.state.value.selectedTabIndex)
    }

    @Test
    fun `process death - TabSelected persists to SavedStateHandle`() = runTest {
        val savedStateHandle = SavedStateHandle()
        viewModel = HomeViewModel(
            observeItems     = ObserveItemsUseCase(fakeRepository),
            toggleFavourite  = ToggleFavouriteUseCase(fakeRepository),
            navigationBus    = fakeNavigationBus,
            savedStateHandle = savedStateHandle,
        )

        viewModel.dispatch(HomeAction.TabSelected(1))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, savedStateHandle.get<Int>("home_selected_tab"))
    }

    // ─── Effect tests ─────────────────────────────────────────────────────────

    @Test
    fun `effect - ShowSnackbar emitted when favourite toggle fails`() = runTest {
        fakeRepository.setItems(TestData.items(1))
        fakeRepository.toggleFavouriteResult = Result.Error(
            com.enterprise.core.common.result.AppException.NetworkException("No internet")
        )
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.effects.test {
            viewModel.dispatch(HomeAction.FavouriteToggled("item_1"))
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is HomeEffect.ShowSnackbar)
        }
    }
}