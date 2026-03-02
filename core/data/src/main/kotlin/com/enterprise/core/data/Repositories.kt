package com.enterprise.core.data

import com.enterprise.core.common.result.Result
import com.enterprise.core.common.result.safeCall
import com.enterprise.core.domain.model.Item
import com.enterprise.core.domain.model.SearchResult
import com.enterprise.core.domain.model.UserProfile
import com.enterprise.core.domain.repository.ItemRepository
import com.enterprise.core.domain.repository.SearchRepository
import com.enterprise.core.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

// ─── DTOs ─────────────────────────────────────────────────────────────────────
// DTOs live in :data and are NEVER exposed to :domain or :feature

@Serializable
data class ItemDto(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val isFavourite: Boolean = false,
)

@Serializable
data class UserProfileDto(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String,
    val bio: String = "",
)

// ─── Mappers ──────────────────────────────────────────────────────────────────

fun ItemDto.toDomain() = Item(
    id          = id,
    title       = title,
    description = description,
    imageUrl    = imageUrl,
    isFavourite = isFavourite,
)

fun UserProfileDto.toDomain() = UserProfile(
    id        = id,
    name      = name,
    email     = email,
    avatarUrl = avatarUrl,
    bio       = bio,
)

fun UserProfile.toDto() = UserProfileDto(
    id        = id,
    name      = name,
    email     = email,
    avatarUrl = avatarUrl,
    bio       = bio,
)

// ─── Repository Implementations ───────────────────────────────────────────────

@Singleton
class ItemRepositoryImpl @Inject constructor() : ItemRepository {

    // In-memory cache; replace with Room + RemoteMediator for production
    private val _items = MutableStateFlow<Result<List<Item>>>(Result.Loading)

    init {
        // Seed with fake data until real API is wired
        _items.value = Result.Success(FakeData.items)
    }

    override fun observeItems(): Flow<Result<List<Item>>> = _items.asStateFlow()

    override suspend fun getItem(id: String): Result<Item> = safeCall {
        FakeData.items.first { it.id == id }
    }

    override suspend fun toggleFavourite(itemId: String): Result<Item> = safeCall {
        val current = (_items.value as? Result.Success)?.data ?: emptyList()
        val updated = current.map { if (it.id == itemId) it.copy(isFavourite = !it.isFavourite) else it }
        _items.value = Result.Success(updated)
        updated.first { it.id == itemId }
    }

    override suspend fun refreshItems(): Result<Unit> = safeCall {
        // Simulate network call
        kotlinx.coroutines.delay(500)
        _items.value = Result.Success(FakeData.items)
    }
}

@Singleton
class SearchRepositoryImpl @Inject constructor() : SearchRepository {
    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())

    override suspend fun search(query: String, page: Int): Result<SearchResult> = safeCall {
        val results = FakeData.items.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
        }
        SearchResult(items = results, query = query, totalCount = results.size)
    }

    override fun observeRecentSearches(): Flow<List<String>> = _recentSearches.asStateFlow()

    override suspend fun saveRecentSearch(query: String) {
        val current = _recentSearches.value.toMutableList()
        current.remove(query)
        current.add(0, query)
        _recentSearches.value = current.take(10)
    }
}

@Singleton
class UserRepositoryImpl @Inject constructor() : UserRepository {
    private val _profile = MutableStateFlow<Result<UserProfile>>(Result.Loading)

    init {
        _profile.value = Result.Success(FakeData.profile)
    }

    override suspend fun getProfile(): Result<UserProfile> =
        (_profile.value as? Result.Success)?.let { it } ?: safeCall { FakeData.profile }

    override suspend fun updateProfile(profile: UserProfile): Result<UserProfile> = safeCall {
        _profile.value = Result.Success(profile)
        profile
    }

    override fun observeProfile(): Flow<Result<UserProfile>> = _profile.asStateFlow()
}

// ─── Fake seed data (replace with API calls) ──────────────────────────────────
private object FakeData {
    val items = (1..20).map { i ->
        Item(
            id          = "item_$i",
            title       = "Item Title $i",
            description = "Detailed description for item $i. This showcases the data layer.",
            imageUrl    = "https://picsum.photos/seed/$i/400/300",
            isFavourite = i % 3 == 0,
        )
    }

    val profile = UserProfile(
        id        = "user_001",
        name      = "Alex Johnson",
        email     = "alex@enterprise.com",
        avatarUrl = "https://i.pravatar.cc/150?img=1",
        bio       = "Senior Android Engineer",
    )
}