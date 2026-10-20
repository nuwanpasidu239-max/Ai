package com.example.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.R
import com.example.data.repository.ProjectRepository
import com.example.ui.screens.MainAppScreen
import com.example.ui.screens.SignInScreen
import com.example.ui.viewmodel.AppBuilderViewModel
import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel()
) {
    val currentUser by authViewModel.authStateFlow.collectAsStateWithLifecycle(initialValue = null)
    val user = currentUser

    if (user == null) {
        val authState by authViewModel.uiState.collectAsStateWithLifecycle()
        SignInScreen(
            isLoading = authState is AuthUiState.Loading,
            errorMessage = (authState as? AuthUiState.Error)?.message,
            onSignInClick = { activity -> authViewModel.signInWithGoogle(activity) }
        )
    } else {
        val context = LocalContext.current
        val builderViewModel: AppBuilderViewModel = viewModel(
            key = user.uid,
            factory = viewModelFactory {
                initializer {
                    val dbId = context.getString(R.string.firestore_database_id)
                    val db = FirebaseFirestore.getInstance(dbId)
                    AppBuilderViewModel(
                        repository = ProjectRepository(db),
                        currentUserId = user.uid
                    )
                }
            }
        )

        MainAppScreen(
            user = user,
            viewModel = builderViewModel,
            onSignOut = { authViewModel.signOut() }
        )
    }
}
