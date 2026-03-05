package com.enterprise.feature.home.mvi;

import androidx.lifecycle.SavedStateHandle;
import com.enterprise.core.domain.usecase.ObserveItemsUseCase;
import com.enterprise.core.domain.usecase.ToggleFavouriteUseCase;
import com.enterprise.core.navigation.NavigationEventBus;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<ObserveItemsUseCase> observeItemsProvider;

  private final Provider<ToggleFavouriteUseCase> toggleFavouriteProvider;

  private final Provider<NavigationEventBus> navigationBusProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private HomeViewModel_Factory(Provider<ObserveItemsUseCase> observeItemsProvider,
      Provider<ToggleFavouriteUseCase> toggleFavouriteProvider,
      Provider<NavigationEventBus> navigationBusProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.observeItemsProvider = observeItemsProvider;
    this.toggleFavouriteProvider = toggleFavouriteProvider;
    this.navigationBusProvider = navigationBusProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(observeItemsProvider.get(), toggleFavouriteProvider.get(), navigationBusProvider.get(), savedStateHandleProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<ObserveItemsUseCase> observeItemsProvider,
      Provider<ToggleFavouriteUseCase> toggleFavouriteProvider,
      Provider<NavigationEventBus> navigationBusProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new HomeViewModel_Factory(observeItemsProvider, toggleFavouriteProvider, navigationBusProvider, savedStateHandleProvider);
  }

  public static HomeViewModel newInstance(ObserveItemsUseCase observeItems,
      ToggleFavouriteUseCase toggleFavourite, NavigationEventBus navigationBus,
      SavedStateHandle savedStateHandle) {
    return new HomeViewModel(observeItems, toggleFavourite, navigationBus, savedStateHandle);
  }
}
