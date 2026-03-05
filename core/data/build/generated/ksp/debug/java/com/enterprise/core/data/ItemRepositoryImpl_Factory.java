package com.enterprise.core.data;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class ItemRepositoryImpl_Factory implements Factory<ItemRepositoryImpl> {
  @Override
  public ItemRepositoryImpl get() {
    return newInstance();
  }

  public static ItemRepositoryImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ItemRepositoryImpl newInstance() {
    return new ItemRepositoryImpl();
  }

  private static final class InstanceHolder {
    static final ItemRepositoryImpl_Factory INSTANCE = new ItemRepositoryImpl_Factory();
  }
}
