package com.example.eventymate.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventymate.PreferencesHelper
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = Firebase.auth

    private val prefsHelper = PreferencesHelper(application)

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkAuthState()
    }

    fun signInWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                val authResult = auth.signInWithCredential(credential).await()

                authResult.user?.let { user ->
                    _isLoggedIn.value = true
                    prefsHelper.onboardingCompleted = true
                    _authState.value = AuthState.Authenticated(user.email ?: "")
                } ?: run {
                    _isLoggedIn.value = false
                    _authState.value = AuthState.Error("Google sign-in failed - no user")
                }
            } catch (e: Exception) {
                _isLoggedIn.value = false
                _authState.value = AuthState.Error(e.message ?: "Google sign-in failed")
            }
        }
    }

    fun checkAuthState() {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val currentUser = auth.currentUser

                if (currentUser != null) {
                    when {
                        // Google-authenticated users don't need email verification
                        isGoogleUser(currentUser) -> {
                            handleSuccessfulAuth(currentUser)
                        }
                        // Email/password users must verify their email
                        currentUser.isEmailVerified -> {
                            handleSuccessfulAuth(currentUser)
                        }
                        else -> {
                            _isLoggedIn.value = false
                            _authState.value = AuthState.EmailNotVerified(currentUser.email ?: "")
                        }
                    }
                } else {
                    _isLoggedIn.value = false
                    _authState.value = AuthState.Unauthenticated
                }
            } catch (e: Exception) {
                _isLoggedIn.value = false
                _authState.value = AuthState.Error(e.message ?: "Authentication check failed")
            }
        }
    }

    private fun isGoogleUser(user: FirebaseUser): Boolean {
        return user.providerData.any {
            it.providerId == GoogleAuthProvider.PROVIDER_ID
        }
    }

    private fun handleSuccessfulAuth(user: FirebaseUser) {
        _isLoggedIn.value = true
        prefsHelper.onboardingCompleted = true
        _authState.value = AuthState.Authenticated(user.email ?: "")
    }

    fun signOut() {
        try {
            auth.signOut()
            _authState.value = AuthState.Unauthenticated
            Log.d("AuthViewModel", "User signed out successfully")
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Sign out error: ${e.message}")
            _authState.value = AuthState.Error("Failed to sign out")
        }
    }

    fun sendEmailVerification() {
        viewModelScope.launch {
            try {
                auth.currentUser?.sendEmailVerification()?.await()
                _authState.value = AuthState.EmailVerificationSent
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Failed to send verification email")
            }
        }
    }
    fun resetPassword(email: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                Log.d("AuthViewModel", "Attempting to send password reset email to: $email")
                auth.sendPasswordResetEmail(email).await()
                _authState.value = AuthState.PasswordResetEmailSent
                Log.d("AuthViewModel", "Password reset email sent successfully")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Password reset error: ${e.message}")
                _authState.value = AuthState.Error(e.message ?: "Password reset failed")
            }
        }
    }
}


sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class EmailNotVerified(val email: String) : AuthState()
    object EmailVerificationSent : AuthState()
    object PasswordResetEmailSent : AuthState()
    data class Authenticated(val email: String) : AuthState()
    data class Error(val message: String) : AuthState()
}