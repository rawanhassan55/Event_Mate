package com.example.eventymate.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import com.example.eventymate.data.Note
import com.example.eventymate.presentation.NoteState
import com.example.eventymate.presentation.NotesViewModel
import com.example.eventymate.screens.SettingsScreen
import com.example.eventymate.screens.SplashScreen
import com.example.eventymate.screens.eventadd.CreateEventScreen
import com.example.eventymate.screens.EditProfileScreen
import com.example.eventymate.screens.LovedScreen
import com.example.eventymate.screens.ProfileScreen
import com.example.eventymate.screens.eventadd.CountdownScreen
import java.lang.reflect.Modifier

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
                },
                navController = navController
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
                onNavigateToMain = { navController.navigate("home") },
                onNavigateToVerify = {navController.navigate("verify_email/{email}")}
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
                onLanguageToggle = onLanguageToggle,
                onThemeToggle = { viewModel.toggleTheme() },
                isDarkTheme = viewModel.isDarkTheme.value
            )
        }
        composable("love") {
            LovedScreen(
                state = state,
                viewModel = viewModel,
                isDarkTheme = viewModel.isDarkTheme.value,
                navController = navController,
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
                onSignOut = { navController.navigate("login") },
                isDarkTheme = viewModel.isDarkTheme.value)
        }

        composable("edit_profile") {
            EditProfileScreen(navController,isDarkTheme = viewModel.isDarkTheme.value)
        }

        composable("countdown/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })) {

            backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")?.toIntOrNull() ?: return@composable

            //log
            Log.d("EventNav", "Navigating to Countdown with Event ID: $eventId")

            val noteFlow = viewModel.getEventByIdFlow(eventId).collectAsState(initial = null)

            noteFlow.value?.let { event ->
                //log
                Log.d("EventNav", "Event found: $event")
                CountdownScreen(event = event, navController = navController)
            } ?: run {
                Box(modifier = androidx.compose.ui.Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }



    }
}