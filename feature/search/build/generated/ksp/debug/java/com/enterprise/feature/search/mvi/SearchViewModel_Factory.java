package com.enterprise.feature.search.mvi;

import androidx.lifecycle.SavedStateHandle;
import com.enterprise.core.domain.usecase.SearchItemsUseCase;
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
public final class SearchViewModel_Factory implements Factory<SearchViewModel> {
  private final Provider<SearchItemsUseCase> searchItemsProvider;

  private final Provider<NavigationEventBus> navigationBusProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private SearchViewModel_Factory(Provider<SearchItemsUseCase> searchItemsProvider,
      Provider<NavigationEventBus> navigationBusProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.searchItemsProvider = searchItemsProvider;
    this.navigationBusProvider = navigationBusProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public SearchViewModel get() {
    return newInstance(searchItemsProvider.get(), navigationBusProvider.get(), savedStateHandleProvider.get());
  }

  public static SearchViewModel_Factory create(Provider<SearchItemsUseCase> searchItemsProvider,
      Provider<NavigationEventBus> navigationBusProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new SearchViewModel_Factory(searchItemsProvider, navigationBusProvider, savedStateHandleProvider);
  }

  public static SearchViewModel newInstance(SearchItemsUseCase searchItems,
      NavigationEventBus navigationBus, SavedStateHandle savedStateHandle) {
    return new SearchViewModel(searchItems, navigationBus, savedStateHandle);
  }
}
