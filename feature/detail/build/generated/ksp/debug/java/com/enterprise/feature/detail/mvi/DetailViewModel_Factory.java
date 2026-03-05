package com.enterprise.feature.detail.mvi;

import androidx.lifecycle.SavedStateHandle;
import com.enterprise.core.domain.usecase.GetItemUseCase;
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
public final class DetailViewModel_Factory implements Factory<DetailViewModel> {
  private final Provider<GetItemUseCase> getItemProvider;

  private final Provider<ToggleFavouriteUseCase> toggleFavouriteProvider;

  private final Provider<NavigationEventBus> navigationBusProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private DetailViewModel_Factory(Provider<GetItemUseCase> getItemProvider,
      Provider<ToggleFavouriteUseCase> toggleFavouriteProvider,
      Provider<NavigationEventBus> navigationBusProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.getItemProvider = getItemProvider;
    this.toggleFavouriteProvider = toggleFavouriteProvider;
    this.navigationBusProvider = navigationBusProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public DetailViewModel get() {
    return newInstance(getItemProvider.get(), toggleFavouriteProvider.get(), navigationBusProvider.get(), savedStateHandleProvider.get());
  }

  public static DetailViewModel_Factory create(Provider<GetItemUseCase> getItemProvider,
      Provider<ToggleFavouriteUseCase> toggleFavouriteProvider,
      Provider<NavigationEventBus> navigationBusProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new DetailViewModel_Factory(getItemProvider, toggleFavouriteProvider, navigationBusProvider, savedStateHandleProvider);
  }

  public static DetailViewModel newInstance(GetItemUseCase getItem,
      ToggleFavouriteUseCase toggleFavourite, NavigationEventBus navigationBus,
      SavedStateHandle savedStateHandle) {
    return new DetailViewModel(getItem, toggleFavourite, navigationBus, savedStateHandle);
  }
}
