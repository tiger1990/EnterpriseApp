package com.enterprise.core.domain.repository

import com.enterprise.core.common.result.Result
import com.enterprise.core.domain.model.Item
import com.enterprise.core.domain.model.SearchResult
import com.enterprise.core.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Repository interfaces defined in domain layer.
 * Implementations live in :core:data.
 * This enforces the Dependency Inversion Principle:
 *   domain does NOT depend on data — data depends on domain.
 */
interface ItemRepository {
    fun observeItems(): Flow<Result<List<Item>>>
    suspend fun getItem(id: String): Result<Item>
    suspend fun toggleFavourite(itemId: String): Result<Item>
    suspend fun refreshItems(): Result<Unit>
}

interface SearchRepository {
    suspend fun search(query: String, page: Int = 0): Result<SearchResult>
    fun observeRecentSearches(): Flow<List<String>>
    suspend fun saveRecentSearch(query: String)
}

interface UserRepository {
    suspend fun getProfile(): Result<UserProfile>
    suspend fun updateProfile(profile: UserProfile): Result<UserProfile>
    fun observeProfile(): Flow<Result<UserProfile>>
}