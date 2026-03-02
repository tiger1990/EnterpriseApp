package com.enterprise.core.data.di

import com.enterprise.core.data.ItemRepositoryImpl
import com.enterprise.core.data.SearchRepositoryImpl
import com.enterprise.core.data.UserRepositoryImpl
import com.enterprise.core.domain.repository.ItemRepository
import com.enterprise.core.domain.repository.SearchRepository
import com.enterprise.core.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module binding domain interfaces to data implementations.
 *
 * Lives in :core:data so the :core:domain module remains framework-free.
 * :feature modules depend on :core:domain interfaces, never on :core:data directly.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindItemRepository(impl: ItemRepositoryImpl): ItemRepository

    @Binds
    @Singleton
    abstract fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}