package com.enterprise.core.navigation.di;

import com.enterprise.core.navigation.NavigationEventBus;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("dagger.hilt.android.scopes.ActivityRetainedScoped")
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
public final class NavigationModule_Companion_ProvideNavigationEventBusFactory implements Factory<NavigationEventBus> {
  @Override
  public NavigationEventBus get() {
    return provideNavigationEventBus();
  }

  public static NavigationModule_Companion_ProvideNavigationEventBusFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static NavigationEventBus provideNavigationEventBus() {
    return Preconditions.checkNotNullFromProvides(NavigationModule.Companion.provideNavigationEventBus());
  }

  private static final class InstanceHolder {
    static final NavigationModule_Companion_ProvideNavigationEventBusFactory INSTANCE = new NavigationModule_Companion_ProvideNavigationEventBusFactory();
  }
}
