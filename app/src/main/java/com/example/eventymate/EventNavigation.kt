package com.example.eventymate

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun EventNavigation(
    authViewModel: AuthViewModel,
    eventViewModel: AuthViewModel,
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = "signup"
    ) {
        composable("login") {
            LoginScreen(
                onNavigateToSignUp = { navController.navigate("signup") },
                onNavigateToMain = {
                    navController.navigate("home") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                onNavigateToForgotPassword = { navController.navigate("forgot_password") },
                viewModel = authViewModel,
                onLoginClick = { navController.navigate("home") },
                onGoogleLoginClick = {}
            )
        }

        composable("signup") {
            SignUpScreen(
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToMain = {
                    navController.navigate("home") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                viewModel = authViewModel,
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
                viewModel = eventViewModel,
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}