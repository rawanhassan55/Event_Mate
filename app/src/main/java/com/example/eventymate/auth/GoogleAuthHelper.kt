package com.example.eventymate.auth

import android.content.Context
import android.content.Intent
import com.example.eventymate.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.tasks.await

class GoogleAuthHelper(private val context: Context) {
    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    val signInClient: GoogleSignInClient by lazy {
        GoogleSignIn.getClient(context, gso)
    }

    suspend fun signInWithIntent(intent: Intent): GoogleSignInResult {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            val account = task.getResult(ApiException::class.java)
            GoogleSignInResult.Success(account)
        } catch (e: ApiException) {
            GoogleSignInResult.Error(e)
        }
    }

    fun getSignInIntent(): Intent {
        return signInClient.signInIntent
    }

    suspend fun signOut() {
        signInClient.signOut().await()
    }
}

sealed class GoogleSignInResult {
    data class Success(val account: com.google.android.gms.auth.api.signin.GoogleSignInAccount) : GoogleSignInResult()
    data class Error(val exception: Exception) : GoogleSignInResult()
}