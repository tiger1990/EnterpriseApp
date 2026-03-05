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
public final class UserRepositoryImpl_Factory implements Factory<UserRepositoryImpl> {
  @Override
  public UserRepositoryImpl get() {
    return newInstance();
  }

  public static UserRepositoryImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static UserRepositoryImpl newInstance() {
    return new UserRepositoryImpl();
  }

  private static final class InstanceHolder {
    static final UserRepositoryImpl_Factory INSTANCE = new UserRepositoryImpl_Factory();
  }
}
