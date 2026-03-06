package com.enterprise.app.ui;

import com.enterprise.core.navigation.NavigationEventBus;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<NavigationEventBus> navigationEventBusProvider;

  private MainActivity_MembersInjector(Provider<NavigationEventBus> navigationEventBusProvider) {
    this.navigationEventBusProvider = navigationEventBusProvider;
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectNavigationEventBus(instance, navigationEventBusProvider.get());
  }

  public static MembersInjector<MainActivity> create(
      Provider<NavigationEventBus> navigationEventBusProvider) {
    return new MainActivity_MembersInjector(navigationEventBusProvider);
  }

  @InjectedFieldSignature("com.enterprise.app.ui.MainActivity.navigationEventBus")
  public static void injectNavigationEventBus(MainActivity instance,
      NavigationEventBus navigationEventBus) {
    instance.navigationEventBus = navigationEventBus;
  }
}
