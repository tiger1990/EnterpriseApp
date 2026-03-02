package com.enterprise.core.domain.model

/**
 * Pure domain models — no Android imports, no framework dependencies.
 * These are the canonical data shapes passed between domain → presentation.
 */
data class Item(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val isFavourite: Boolean = false,
)

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String,
    val bio: String = "",
)

data class SearchResult(
    val items: List<Item>,
    val query: String,
    val totalCount: Int,
)