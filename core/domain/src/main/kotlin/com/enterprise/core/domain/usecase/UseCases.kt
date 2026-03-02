package com.enterprise.core.domain.usecase

import com.enterprise.core.common.result.Result
import com.enterprise.core.domain.model.Item
import com.enterprise.core.domain.model.SearchResult
import com.enterprise.core.domain.model.UserProfile
import com.enterprise.core.domain.repository.ItemRepository
import com.enterprise.core.domain.repository.SearchRepository
import com.enterprise.core.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveItemsUseCase @Inject constructor(
    private val repository: ItemRepository,
) : FlowUseCase<Unit, List<Item>>() {
    override fun execute(params: Unit): Flow<Result<List<Item>>> =
        repository.observeItems()
}

class GetItemUseCase @Inject constructor(
    private val repository: ItemRepository,
) : UseCase<String, Item>() {
    override suspend fun execute(params: String): Result<Item> =
        repository.getItem(params)
}

class ToggleFavouriteUseCase @Inject constructor(
    private val repository: ItemRepository,
) : UseCase<String, Item>() {
    override suspend fun execute(params: String): Result<Item> =
        repository.toggleFavourite(params)
}

class SearchItemsUseCase @Inject constructor(
    private val repository: SearchRepository,
) : UseCase<SearchItemsUseCase.Params, SearchResult>() {
    data class Params(val query: String, val page: Int = 0)
    override suspend fun execute(params: Params): Result<SearchResult> =
        repository.search(params.query, params.page)
}

class GetProfileUseCase @Inject constructor(
    private val repository: UserRepository,
) : NoParamsUseCase<UserProfile>() {
    override suspend fun execute(params: Unit): Result<UserProfile> =
        repository.getProfile()
}

class UpdateProfileUseCase @Inject constructor(
    private val repository: UserRepository,
) : UseCase<UserProfile, UserProfile>() {
    override suspend fun execute(params: UserProfile): Result<UserProfile> =
        repository.updateProfile(params)
}