package com.enterprise.feature.profile.mvi

import com.enterprise.core.domain.model.UserProfile

/**
 * UI-only representation of a UserProfile for the Profile screen.
 *
 * Concrete benefits over passing UserProfile directly:
 * - [hasBio]: computed once so `profile.bio.isNotEmpty()` is not scattered in Compose
 * - Future: `avatarUrl` could be omitted if the avatar widget is removed, without
 *   touching the domain model. Conversely, a `displayInitials: String` computed
 *   from `name` would live here, not as logic in a Composable.
 */
data class ProfileUiModel(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String,
    val bio: String,
    // UI-specific: avoids bio.isNotEmpty() conditional scattered across Composables
    val hasBio: Boolean,
)

fun UserProfile.toUiModel() = ProfileUiModel(
    id        = id,
    name      = name,
    email     = email,
    avatarUrl = avatarUrl,
    bio       = bio,
    hasBio    = bio.isNotEmpty(),
)

// Reverse mapper for UpdateProfileUseCase — maps presentation state back to domain
fun ProfileUiModel.toDomain() = UserProfile(
    id        = id,
    name      = name,
    email     = email,
    avatarUrl = avatarUrl,
    bio       = bio,
)
