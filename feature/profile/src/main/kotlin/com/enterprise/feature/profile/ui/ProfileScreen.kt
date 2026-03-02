package com.enterprise.feature.profile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.composable
import com.enterprise.core.common.mvi.MviViewModel
import com.enterprise.core.common.mvi.Reducer
import com.enterprise.core.common.mvi.UiAction
import com.enterprise.core.common.mvi.UiEffect
import com.enterprise.core.common.mvi.UiState
import com.enterprise.core.common.result.Result
import com.enterprise.core.domain.model.UserProfile
import com.enterprise.core.domain.usecase.GetProfileUseCase
import com.enterprise.core.domain.usecase.ObserveItemsUseCase
import com.enterprise.core.navigation.EditProfileRoute
import com.enterprise.core.navigation.NavGraphBuilderScope
import com.enterprise.core.navigation.NavigationEvent
import com.enterprise.core.navigation.NavigationEventBus
import com.enterprise.core.navigation.ProfileRoute
import com.enterprise.core.navigation.SettingsRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
// ═══════════════════════════ UI ═══════════════════════════════════════════════

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ProfileContent(state = state, onAction = viewModel::dispatch)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProfileContent(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = { onAction(ProfileAction.BackPressed) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
            )
        },
        floatingActionButton = {
            if (state.profile != null) {
                FloatingActionButton(onClick = { onAction(ProfileAction.EditProfileTapped) }) {
                    Icon(Icons.Default.Edit, "Edit Profile")
                }
            }
        },
    ) { paddingValues ->
        when {
            state.isLoading -> Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) { CircularProgressIndicator() }

            state.profile != null -> ProfileBody(
                profile  = state.profile,
                onSettings = { onAction(ProfileAction.SettingsTapped) },
                modifier = Modifier.padding(paddingValues),
            )

            else -> Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) { Text(state.errorMessage ?: "No profile") }
        }
    }
}

@Composable
private fun ProfileBody(
    profile: UserProfile,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier            = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(32.dp))
        Text(profile.name, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(profile.email, style = MaterialTheme.typography.bodyLarge,
             color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (profile.bio.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text(profile.bio, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(32.dp))
        Button(onClick = onSettings) { Text("Settings") }
    }
}