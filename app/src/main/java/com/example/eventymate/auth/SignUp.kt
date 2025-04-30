package com.example.eventymate.auth

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventymate.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


@Composable
fun SignUpScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToMain: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rePassword by remember { mutableStateOf("") }
    val onPasswordVisibilityToggle: (Boolean) -> Unit = {}
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val auth: FirebaseAuth = Firebase.auth

    val sharedPreferences = remember {
        context.getSharedPreferences("EventlyPrefs", Context.MODE_PRIVATE)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.event_logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(250.dp)
                //.padding(bottom = 16.dp)
        )

        Text(
            text = "Sign Up",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0XFF4A5182),
            //modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Create your new account",
            fontSize = 16.sp,
            color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
        )


        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = {
                Row {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Name",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Name")
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

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = {
                Row {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Email")
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
            label = "Enter Password"
        )

        PasswordTextField(
            value = rePassword,
            onValueChange = { rePassword = it },
            label = "Enter Password Again"
        )


        Button(
            onClick = {
                when {
                    email.isBlank() || password.isBlank() || rePassword.isBlank() || name.isBlank() -> {
                        errorMessage = "Please fill in all fields"
                    }

                    password.length < 6 -> {
                        errorMessage = "Password must be at least 6 characters"
                    }

                    password != rePassword -> {
                        errorMessage = "Passwords don't match"
                    }

                    else -> {
                        isLoading = true
                        errorMessage = null
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    user?.sendEmailVerification()
                                        ?.addOnCompleteListener { verificationTask ->
                                            isLoading = false
                                            if (verificationTask.isSuccessful) {
                                                onNavigateToLogin()
                                            } else {
                                                errorMessage = "Failed to send verification email"
                                            }
                                        }
                                } else {
                                    isLoading = false
                                    errorMessage = task.exception?.message ?: "Signup failed"
                                }
                            }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0XFF4A5182),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 2.dp
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        val annotatedText = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )
            ) {
                append("Already Have Account? ")
            }
            pushStringAnnotation(
                tag = "LOGIN_TAG",
                annotation = "login_clicked"
            )
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    color = Color(0XFF4A5182),
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append("Login")
            }
            pop()
        }

        ClickableText(
            text = annotatedText,
            modifier = Modifier.padding(top = 32.dp),
            onClick = { offset ->
                annotatedText.getStringAnnotations(
                    tag = "LOGIN_TAG",
                    start = offset,
                    end = offset
                ).firstOrNull()?.let {
                    onNavigateToLogin()
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        var selectedFlag by remember { mutableStateOf("egypt") }

        FlagToggle(selectedFlag = selectedFlag) { newFlag ->
            selectedFlag = newFlag
        }

    }
}

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Password",
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Row {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Password",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(label)
            }
        },
        visualTransformation = if (isPasswordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(
                onClick = { isPasswordVisible = !isPasswordVisible },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isPasswordVisible) {
                            R.drawable.ic_eye
                        } else {
                            R.drawable.ic_eye
                        }
                    ),
                    contentDescription = if (isPasswordVisible) {
                        "Hide password"
                    } else {
                        "Show password"
                    },
                    tint = Color(0XFF4A5182)
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0XFF4A5182),
            unfocusedBorderColor = Color(0XFF4A5182),
            focusedLabelColor = Color(0XFF4A5182),
            unfocusedLabelColor = Color.Gray,
            cursorColor = Color(0XFF4A5182),
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.DarkGray
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        singleLine = true
    )
}