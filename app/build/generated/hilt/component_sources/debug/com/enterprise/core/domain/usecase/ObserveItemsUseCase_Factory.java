package com.enterprise.core.domain.usecase;

import com.enterprise.core.domain.repository.ItemRepository;
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
public final class ObserveItemsUseCase_Factory implements Factory<ObserveItemsUseCase> {
  private final Provider<ItemRepository> repositoryProvider;

  private ObserveItemsUseCase_Factory(Provider<ItemRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ObserveItemsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ObserveItemsUseCase_Factory create(Provider<ItemRepository> repositoryProvider) {
    return new ObserveItemsUseCase_Factory(repositoryProvider);
  }

  public static ObserveItemsUseCase newInstance(ItemRepository repository) {
    return new ObserveItemsUseCase(repository);
  }
}
