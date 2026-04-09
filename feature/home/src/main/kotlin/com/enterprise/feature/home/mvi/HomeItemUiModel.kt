package com.enterprise.feature.home.mvi

import com.enterprise.core.domain.model.Item

/**
 * UI-only representation of an Item for the Home screen.
 *
 * ─── Why this exists ─────────────────────────────────────────────────────────
 * Domain models must not flow into UiState — the presentation layer should not
 * be coupled to the domain model's structure. This model is the contract between
 * HomeViewModel and HomeScreen; the domain Item is an implementation detail of
 * the data layer.
 *
 * ─── What makes it different from Item ───────────────────────────────────────
 * - Omits fields the screen never renders (future: ratings, tags, etc.)
 * - Adds [favouriteContentDescription]: computed once in the ViewModel so that
 *   accessibility strings are not scattered as if-else expressions in Compose.
 * - When Item gains a `publishedDate: LocalDate`, the formatted string goes here
 *   (e.g. "Apr 9 2026"), keeping date formatting out of Composables.
 */
data class HomeItemUiModel(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val isFavourite: Boolean,
    // UI-specific: pre-computed so icon content descriptions are not inline if-else in Compose
    val favouriteContentDescription: String,
)

// ─── Mapper ───────────────────────────────────────────────────────────────────
// Lives here (feature layer), not in core:domain — the domain model has no
// knowledge of how it is displayed.

fun Item.toHomeUiModel() = HomeItemUiModel(
    id          = id,
    title       = title,
    description = description,
    imageUrl    = imageUrl,
    isFavourite = isFavourite,
    favouriteContentDescription = if (isFavourite) "Remove from favourites" else "Add to favourites",
)
