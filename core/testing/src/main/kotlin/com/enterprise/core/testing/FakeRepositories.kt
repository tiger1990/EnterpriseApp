package com.enterprise.core.testing

import com.enterprise.core.common.result.Result
import com.enterprise.core.domain.model.Item
import com.enterprise.core.domain.model.SearchResult
import com.enterprise.core.domain.model.UserProfile
import com.enterprise.core.domain.repository.ItemRepository
import com.enterprise.core.domain.repository.SearchRepository
import com.enterprise.core.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

// ─── Fake Repositories ────────────────────────────────────────────────────────

class FakeItemRepository : ItemRepository {
    private val _items = MutableStateFlow<Result<List<Item>>>(Result.Loading)
    var toggleFavouriteResult: Result<Item>? = null

    fun setItems(items: List<Item>) { _items.value = Result.Success(items) }
    fun setError(message: String)   { _items.value = Result.Error(com.enterprise.core.common.result.AppException.UnknownException(Exception(message))) }

    override fun observeItems(): Flow<Result<List<Item>>> = _items
    override suspend fun getItem(id: String): Result<Item> =
        (_items.value as? Result.Success)?.data?.firstOrNull { it.id == id }
            ?.let { Result.Success(it) }
            ?: Result.Error(com.enterprise.core.common.result.AppException.NotFoundException(id))
    override suspend fun toggleFavourite(itemId: String): Result<Item> =
        toggleFavouriteResult ?: Result.Error(com.enterprise.core.common.result.AppException.UnknownException(Exception("Not set")))
    override suspend fun refreshItems(): Result<Unit> = Result.Success(Unit)
}

class FakeSearchRepository : SearchRepository {
    var searchResult: Result<SearchResult> = Result.Success(SearchResult(emptyList(), "", 0))

    override suspend fun search(query: String, page: Int): Result<SearchResult> = searchResult
    override fun observeRecentSearches(): Flow<List<String>> = MutableStateFlow(emptyList())
    override suspend fun saveRecentSearch(query: String) {}
}

class FakeUserRepository : UserRepository {
    private val _profile = MutableStateFlow<Result<UserProfile>>(Result.Loading)

    fun setProfile(profile: UserProfile) { _profile.value = Result.Success(profile) }

    override suspend fun getProfile(): Result<UserProfile> = _profile.value
    override suspend fun updateProfile(profile: UserProfile): Result<UserProfile> {
        _profile.value = Result.Success(profile)
        return Result.Success(profile)
    }
    override fun observeProfile(): Flow<Result<UserProfile>> = _profile
}

// ─── Test data builders ───────────────────────────────────────────────────────

object TestData {
    fun item(
        id          : String  = "test_1",
        title       : String  = "Test Item",
        description : String  = "Test description",
        imageUrl    : String  = "",
        isFavourite : Boolean = false,
    ) = Item(id, title, description, imageUrl, isFavourite)

    fun items(count: Int = 5) = (1..count).map {
        item(id = "item_$it", title = "Item $it", description = "Desc $it")
    }

    fun profile(
        id        : String = "user_1",
        name      : String = "Test User",
        email     : String = "test@test.com",
        avatarUrl : String = "",
        bio       : String = "Test bio",
    ) = UserProfile(id, name, email, avatarUrl, bio)
}