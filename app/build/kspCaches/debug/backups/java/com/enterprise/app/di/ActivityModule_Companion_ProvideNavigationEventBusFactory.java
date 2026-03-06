package com.enterprise.app.di;

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
public final class ActivityModule_Companion_ProvideNavigationEventBusFactory implements Factory<NavigationEventBus> {
  @Override
  public NavigationEventBus get() {
    return provideNavigationEventBus();
  }

  public static ActivityModule_Companion_ProvideNavigationEventBusFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static NavigationEventBus provideNavigationEventBus() {
    return Preconditions.checkNotNullFromProvides(ActivityModule.Companion.provideNavigationEventBus());
  }

  private static final class InstanceHolder {
    static final ActivityModule_Companion_ProvideNavigationEventBusFactory INSTANCE = new ActivityModule_Companion_ProvideNavigationEventBusFactory();
  }
}
