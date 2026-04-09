package com.enterprise.feature.profile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.enterprise.core.tokens.R
import com.enterprise.feature.profile.mvi.ProfileUiModel
import com.enterprise.feature.profile.mvi.ProfileAction
import com.enterprise.feature.profile.mvi.ProfileState
import com.enterprise.feature.profile.mvi.ProfileViewModel

// ═══════════════════════════ UI ═══════════════════════════════════════════════

@Composable
fun ProfileScreen() {
    val viewModel: ProfileViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { /* ProfileEffect has no variants yet — collector ready for future effects */ }
    }

    ProfileContent(state = state, snackbarHost = snackBarHostState, onAction = viewModel::dispatch)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProfileContent(
    state: ProfileState,
    snackbarHost: SnackbarHostState,
    onAction: (ProfileAction) -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHost) },
        topBar = {
            //CenterAlignedTopAppBar( // Or TopAppBar for left-aligned
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = { onAction(ProfileAction.BackPressed) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Back"
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            if (state.profile != null) {
                FloatingActionButton(
                    onClick = { onAction(ProfileAction.EditProfileTapped) },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit_profile),
                        contentDescription = "Edit Profile"
                    )
                }
            }
        },
    ) { paddingValues ->
        when {
            state.isLoading -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) { CircularProgressIndicator() }

            state.profile != null -> ProfileBody(
                profile = state.profile,
                onSettings = { onAction(ProfileAction.SettingsTapped) },
                modifier = Modifier.padding(paddingValues),
            )

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) { Text(state.errorMessage ?: "No profile") }
        }
    }
}

@Composable
private fun ProfileBody(
    profile: ProfileUiModel,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(32.dp))
        Text(profile.name, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            profile.email,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (profile.hasBio) {
            Spacer(Modifier.height(16.dp))
            Text(profile.bio, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(32.dp))
        Button(onClick = onSettings) { Text("Settings") }
    }
}
