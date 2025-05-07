package com.example.eventymate.screens


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.eventymate.R
import com.example.eventymate.auth.AuthState
import com.example.eventymate.auth.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(
    navController: NavHostController,
    auth: FirebaseAuth = Firebase.auth,
    authViewModel: AuthViewModel = viewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToVerification: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    val authState by authViewModel.authState.collectAsState()

    val user = auth.currentUser
    LaunchedEffect(Unit) {
        if (user != null && user.isEmailVerified) {
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                delay(2000)
                onNavigateToHome()
            }
            is AuthState.EmailNotVerified -> {
                delay(1500)
                onNavigateToVerification((authState as AuthState.EmailNotVerified).email)
            }
            is AuthState.Unauthenticated -> {
                delay(1500)
                onNavigateToLogin()
            }
            else -> {}
        }
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.event_logo),
                contentDescription = "Event Mate Logo"
            )
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(color = Color.Blue)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

