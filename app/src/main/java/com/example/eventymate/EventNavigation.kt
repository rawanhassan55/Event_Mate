package com.example.eventymate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eventymate.auth.AuthViewModel
import com.example.eventymate.auth.ForgotPasswordScreen
import com.example.eventymate.auth.LoginScreen
import com.example.eventymate.auth.SignUpScreen
import com.example.eventymate.presentation.AddNoteScreen
import com.example.eventymate.presentation.NoteState
import com.example.eventymate.presentation.NotesScreen
import com.example.eventymate.presentation.NotesViewModel
import com.example.eventymate.screens.eventadd.CreateEventScreen
import com.example.eventymate.ui.theme.EditProfileScreen
import com.example.eventymate.ui.theme.ProfileScreen

//import com.example.eventymate.screens.eventadd.CreateEventScreen

@Composable
fun EventNavigation(
    authViewModel: AuthViewModel,
    eventViewModel: AuthViewModel,
    state : NoteState,
    viewModel : NotesViewModel
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val prefsHelper = remember { PreferencesHelper(context) }

    NavHost(
        navController = navController,
        startDestination = "signup"
    ) {

        composable("splash") {
            SplashScreen(navController)
        }

        composable("setting") {
            SettingsScreen(

            )
        }
        composable("login") {
            LoginScreen(
                onNavigateToSignUp = { navController.navigate("signup") },
                onNavigateToMain = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToForgotPassword = { navController.navigate("forgot_password") },
                viewModel = authViewModel,
                onSignInSuccess = { navController.navigate("home") })
        }

        composable("signup") {
            SignUpScreen(
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToMain = {
                    if (prefsHelper.onboardingCompleted) {
                        navController.navigate("home") {
                            popUpTo("signup") { inclusive = true }
                        }
                    }
                },
                viewModel = authViewModel
            )
        }

        composable("forgot_password") {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = authViewModel
            )
        }

        composable("home") {
            HomeScreen(
                navController = navController,
                onCreateEventNavigation = { navController.navigate("createEvent") },
                state = state,
                viewModel = viewModel
            )
        }

        composable("createEvent") {
            CreateEventScreen(
                onBackClick = { navController.popBackStack() },
                state = state,
                navController = navController,
                onEvent = viewModel::onEvent
            )
        }

        composable("profile") {
            ProfileScreen(navController)
        }

        composable("edit_profile") {
            EditProfileScreen(navController)
        }



//        composable("AddNoteScreen") {
//            AddNoteScreen(
//                state = state,
//                navController = navController,
//                onEvent = viewModel::onEvent
//            )
//        }


        /*composable("home") {
            HomeScreen(
                navController = navController,
                viewModel = eventViewModel,
                onSettingNavigation = { navController.navigate("setting") },
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }*/
    }
}