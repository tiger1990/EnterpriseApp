package com.enterprise.feature.search.mvi

import com.enterprise.core.domain.model.Item

/**
 * UI-only representation of an Item for the Search results list.
 *
 * Deliberately narrower than [Item] — search results render only title and
 * description. imageUrl, isFavourite, and any future domain fields are excluded:
 * the search screen has no favourite toggle and no image column.
 *
 * This is the concrete benefit of the UiModel pattern: when Item gains 10 new
 * fields (ratings, tags, pricing), SearchState holds none of them.
 */
data class SearchItemUiModel(
    val id: String,
    val title: String,
    val description: String,
)

fun Item.toSearchUiModel() = SearchItemUiModel(
    id          = id,
    title       = title,
    description = description,
)
