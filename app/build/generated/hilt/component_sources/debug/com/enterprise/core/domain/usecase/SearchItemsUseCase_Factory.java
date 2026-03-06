package com.enterprise.core.domain.usecase;

import com.enterprise.core.domain.repository.SearchRepository;
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
public final class SearchItemsUseCase_Factory implements Factory<SearchItemsUseCase> {
  private final Provider<SearchRepository> repositoryProvider;

  private SearchItemsUseCase_Factory(Provider<SearchRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public SearchItemsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static SearchItemsUseCase_Factory create(Provider<SearchRepository> repositoryProvider) {
    return new SearchItemsUseCase_Factory(repositoryProvider);
  }

  public static SearchItemsUseCase newInstance(SearchRepository repository) {
    return new SearchItemsUseCase(repository);
  }
}
