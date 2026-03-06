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
public final class ToggleFavouriteUseCase_Factory implements Factory<ToggleFavouriteUseCase> {
  private final Provider<ItemRepository> repositoryProvider;

  private ToggleFavouriteUseCase_Factory(Provider<ItemRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ToggleFavouriteUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ToggleFavouriteUseCase_Factory create(Provider<ItemRepository> repositoryProvider) {
    return new ToggleFavouriteUseCase_Factory(repositoryProvider);
  }

  public static ToggleFavouriteUseCase newInstance(ItemRepository repository) {
    return new ToggleFavouriteUseCase(repository);
  }
}
