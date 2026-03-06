package com.enterprise.core.domain.usecase;

import com.enterprise.core.domain.repository.UserRepository;
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
public final class GetProfileUseCase_Factory implements Factory<GetProfileUseCase> {
  private final Provider<UserRepository> repositoryProvider;

  private GetProfileUseCase_Factory(Provider<UserRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetProfileUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetProfileUseCase_Factory create(Provider<UserRepository> repositoryProvider) {
    return new GetProfileUseCase_Factory(repositoryProvider);
  }

  public static GetProfileUseCase newInstance(UserRepository repository) {
    return new GetProfileUseCase(repository);
  }
}
