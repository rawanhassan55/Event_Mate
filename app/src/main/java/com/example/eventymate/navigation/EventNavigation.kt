package com.example.eventymate.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.eventymate.HomeScreen
import com.example.eventymate.auth.AuthViewModel
import com.example.eventymate.auth.EmailVerificationScreen
import com.example.eventymate.auth.ForgotPasswordScreen
import com.example.eventymate.auth.LoginScreen
import com.example.eventymate.auth.PreferencesHelper
import com.example.eventymate.auth.SignUpScreen
import com.example.eventymate.presentation.NoteState
import com.example.eventymate.presentation.NotesViewModel
import com.example.eventymate.screens.SettingsScreen
import com.example.eventymate.screens.SplashScreen
import com.example.eventymate.screens.eventadd.CreateEventScreen
import com.example.eventymate.screens.EditProfileScreen
import com.example.eventymate.screens.ProfileScreen

//import com.example.eventymate.screens.eventadd.CreateEventScreen

@Composable
fun EventNavigation(
    authViewModel: AuthViewModel,
    state: NoteState,
    viewModel: NotesViewModel,
    onLanguageToggle: (String) -> Unit,
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val prefsHelper = remember { PreferencesHelper(context) }


    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        composable("splash") {
            SplashScreen(
                onNavigateToHome = { navController.navigate("home") },
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToVerification = { email ->
                    navController.navigate("verify_email/$email")
                }
            )
        }

        composable(
            "verify_email/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            EmailVerificationScreen(
                email = email,
                onBackToLogin = {
                    navController.navigate("login") {
                        popUpTo("verify_email/{email}") { inclusive = true }
                    }
                },
                onVerified = {
                    navController.navigate("home") {
                        popUpTo("verify_email/{email}") { inclusive = true }
                    }
                }
            )
        }

        composable("setting") {
            SettingsScreen(

            )
        }
        composable("login") {
            LoginScreen(
                onSignInSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToSignUp = { navController.navigate("signup") },
                onNavigateToForgotPassword = { navController.navigate("forgot_password") },
                viewModel = authViewModel,
                onNavigateToMain = { navController.navigate("home") }
            )
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
                viewModel = viewModel,
                onLanguageToggle = onLanguageToggle
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
            ProfileScreen(navController,
                onSignOut = { navController.navigate("login") })
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