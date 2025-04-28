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
import com.example.eventymate.screens.eventadd.CreateEventScreen

//import com.example.eventymate.screens.eventadd.CreateEventScreen

@Composable
fun EventNavigation(
    authViewModel: AuthViewModel,
    eventViewModel: AuthViewModel,
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
                onGoogleLoginClick = {}
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
                onCreateEventNavigation = { navController.navigate("createEvent") }
            )
        }

        composable("createEvent") {
            CreateEventScreen(
                onBackClick = { navController.popBackStack() }
            )
        }


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