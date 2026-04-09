package com.enterprise.feature.detail.mvi

import com.enterprise.core.domain.model.Item

/**
 * UI-only representation of an Item for the Detail screen.
 *
 * Adds [idLabel]: the "Item ID: xxx" string is display logic — it belongs here,
 * not interpolated inline inside a Composable. When the domain model gains a
 * `publishedDate`, format it to a string here and expose `formattedDate: String`.
 */
data class DetailItemUiModel(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val isFavourite: Boolean,
    // UI-specific: formatted label, computed once so Compose stays logic-free
    val idLabel: String,
)

fun Item.toDetailUiModel() = DetailItemUiModel(
    id          = id,
    title       = title,
    description = description,
    imageUrl    = imageUrl,
    isFavourite = isFavourite,
    idLabel     = "Item ID: $id",
)
