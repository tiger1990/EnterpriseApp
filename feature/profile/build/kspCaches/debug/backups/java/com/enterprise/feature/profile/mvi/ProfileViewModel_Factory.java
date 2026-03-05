package com.enterprise.feature.profile.mvi;

import androidx.lifecycle.SavedStateHandle;
import com.enterprise.core.domain.usecase.GetProfileUseCase;
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
public final class ProfileViewModel_Factory implements Factory<ProfileViewModel> {
  private final Provider<GetProfileUseCase> getProfileProvider;

  private final Provider<NavigationEventBus> navigationBusProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private ProfileViewModel_Factory(Provider<GetProfileUseCase> getProfileProvider,
      Provider<NavigationEventBus> navigationBusProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.getProfileProvider = getProfileProvider;
    this.navigationBusProvider = navigationBusProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public ProfileViewModel get() {
    return newInstance(getProfileProvider.get(), navigationBusProvider.get(), savedStateHandleProvider.get());
  }

  public static ProfileViewModel_Factory create(Provider<GetProfileUseCase> getProfileProvider,
      Provider<NavigationEventBus> navigationBusProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new ProfileViewModel_Factory(getProfileProvider, navigationBusProvider, savedStateHandleProvider);
  }

  public static ProfileViewModel newInstance(GetProfileUseCase getProfile,
      NavigationEventBus navigationBus, SavedStateHandle savedStateHandle) {
    return new ProfileViewModel(getProfile, navigationBus, savedStateHandle);
  }
}
