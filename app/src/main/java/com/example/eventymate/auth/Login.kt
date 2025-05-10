package com.example.eventymate.auth

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.eventymate.R
import com.example.eventymate.locale.LocaleHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onSignInSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToVerify: () -> Unit,
    onNavigateToMain: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    viewModel: AuthViewModel = viewModel(),
) {
    val navController = rememberNavController()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val googleAuthHelper = remember { GoogleAuthHelper(context) }
    val authState by viewModel.authState.collectAsState()
    val activity = context as? Activity
    var selectedLanguage by remember { mutableStateOf(LocaleHelper.getPersistedLanguage(context)) }

    fun changeLanguage(language: String) {
        selectedLanguage = language
        LocaleHelper.persistLanguage(context, language)
        activity?.recreate()
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                Log.d("AuthDebug", "Login successful, navigating to home")
                onSignInSuccess()
            }

            is AuthState.EmailNotVerified -> {
                Log.d("AuthDebug", "Email not verified - show warning")
                errorMessage = "Please verify your email."

            }

            else -> {}
        }
    }

    val auth: FirebaseAuth = Firebase.auth

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        coroutineScope.launch {
            val signInResult = googleAuthHelper.signInWithIntent(result.data ?: return@launch)
            when (signInResult) {
                is GoogleSignInResult.Success -> {
                    viewModel.signInWithGoogle(signInResult.account)
                }

                is GoogleSignInResult.Error -> {
                    errorMessage = signInResult.exception.message ?: "Google sign-in failed"
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .background(Color.White)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.event_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(280.dp)
                    .height(50.dp)
                    .width(50.dp)
            )

            Text(
                text = stringResource(id = R.string.welcome_back),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0XFF4A5182),
            )

            Text(
                text = stringResource(id = R.string.login_to_account),
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = {
                    Row {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = stringResource(id = R.string.email),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(id = R.string.email))
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0XFF4A5182),
                    unfocusedBorderColor = Color(0XFF4A5182),
                    focusedLabelColor = Color(0XFF4A5182),
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = Color(0XFF4A5182),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.DarkGray,
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true
            )

            PasswordTextField(
                value = password,
                onValueChange = { password = it },
                label = stringResource(id = R.string.login_to_account),
            )

            TextButton(onClick = { onNavigateToForgotPassword() }) {
                Text(
                    text = stringResource(id = R.string.forgot_password), Modifier.fillMaxWidth(1f),
                    style = TextStyle(color = Color(0XFF4A5182))
                )
            }

            Button(
                onClick = {
                    isLoading = true
                    errorMessage = null

                    // Validate Email format
                    if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        errorMessage = "Please enter a valid email address"
                        isLoading = false
                        return@Button
                    }

                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                if (user != null && user.isEmailVerified) {
                                    onNavigateToMain()
                                } else {
                                    navController.navigate("verify_email/{email}")
                                }
                            } else {
                                errorMessage = task.exception?.message ?: "Login failed"
                            }
                        }
                },
                enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(Color(0XFF4A5182))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(stringResource(id = R.string.login), style = TextStyle(color = Color.White))
                }
            }

            Row(
                modifier = Modifier.padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Color.Gray.copy(alpha = 0.3f)
                )
                Text(
                    text = stringResource(id = R.string.or),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = Color.Gray
                )
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Color.Gray.copy(alpha = 0.3f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(id = R.string.no_account))
                TextButton(onClick = onNavigateToSignUp) {
                    Text(
                        stringResource(id = R.string.sign_up),
                        style = TextStyle(color = Color(0XFF4A5182))
                    )
                }
            }

            OutlinedButton(
                onClick = {
                    googleSignInLauncher.launch(googleAuthHelper.getSignInIntent())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0XFF4A5182)
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0XFF4A5182))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.google_icon),
                        contentDescription = "Google logo",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        stringResource(id = R.string.login_with_google),
                        style = TextStyle(color = Color(0XFF4A5182))
                    )
                }
            }

            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            FlagToggle(
                selectedFlag = when (selectedLanguage) {
                    "ar" -> "egypt"
                    else -> "usa"
                }
            ) { flag ->
                changeLanguage(
                    when (flag) {
                        "egypt" -> "ar"
                        else -> "en"
                    }
                )
            }
        }
    }
}

@Composable
fun FlagToggle(
    selectedFlag: String,
    onFlagSelected: (String) -> Unit,
) {
    val flags = listOf("usa", "egypt")
    val flagImages = mapOf(
        "usa" to R.drawable.english,
        "egypt" to R.drawable.arabic
    )

    Box(
        modifier = Modifier
            .padding(24.dp)
            .background(Color(0XFF4A5182), shape = RoundedCornerShape(50))
            .border(2.dp, Color(0XFF4A5182), shape = RoundedCornerShape(50))
            .padding(horizontal = 2.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            flags.forEach { flag ->
                val isSelected = flag == selectedFlag
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) Color(0XFF4A5182) else Color.Transparent,
                            CircleShape
                        )
                        .clickable { onFlagSelected(flag) }
                        .padding(2.dp)
                ) {
                    Image(
                        painter = painterResource(id = flagImages[flag]!!),
                        contentDescription = "$flag flag",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.White, CircleShape)
                    )
                }
            }
        }
    }
}
