package com.enterprise.feature.seettings.mvi;

import androidx.lifecycle.SavedStateHandle;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<NavigationEventBus> navigationBusProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private SettingsViewModel_Factory(Provider<NavigationEventBus> navigationBusProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.navigationBusProvider = navigationBusProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(navigationBusProvider.get(), savedStateHandleProvider.get());
  }

  public static SettingsViewModel_Factory create(Provider<NavigationEventBus> navigationBusProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new SettingsViewModel_Factory(navigationBusProvider, savedStateHandleProvider);
  }

  public static SettingsViewModel newInstance(NavigationEventBus navigationBus,
      SavedStateHandle savedStateHandle) {
    return new SettingsViewModel(navigationBus, savedStateHandle);
  }
}
