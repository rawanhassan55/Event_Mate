package com.example.eventymate

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    onSignOut: () -> Unit = {}
){}