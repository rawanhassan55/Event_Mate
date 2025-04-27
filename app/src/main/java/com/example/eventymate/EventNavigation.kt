package com.example.eventymate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eventymate.ui.theme.SplashScreen

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
        startDestination = "splash"
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
                onNavigateToMain = { navController.navigate("home") {
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
                viewModel = eventViewModel,
                onSettingNavigation = { navController.navigate("setting") },
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