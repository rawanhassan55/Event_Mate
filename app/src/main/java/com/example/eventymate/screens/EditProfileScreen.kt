package com.example.eventymate.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventymate.ui.theme.ThemeColors
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun EditProfileScreen(
    navController: NavController, isDarkTheme: Boolean,
) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var genderExpanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    val colors = if (isDarkTheme) ThemeColors.Night else ThemeColors.Day
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val db = FirebaseFirestore.getInstance()

    // Load user data when screen opens
    LaunchedEffect(Unit) {
        if (userId != null) {
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        name = document.getString("name") ?: ""
                        username = document.getString("username") ?: ""
                        gender = document.getString("gender") ?: "Female" // Default if not set
                        phone = document.getString("phone") ?: ""
                        email = document.getString("email") ?: ""
                    }
                    isLoading = false
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error loading profile", Toast.LENGTH_SHORT).show()
                    isLoading = false
                }
        } else {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(colors.secondary)
    ) {
        if (isLoading) {
            CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
        } else {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(colors.secondary)
                    .clickable { navController.popBackStack() }
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Edit Profile",
                    fontSize = 20.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = {},
                    label = { Text("Gender") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { genderExpanded = true },
                    enabled = false
                )
                DropdownMenu(
                    expanded = genderExpanded,
                    onDismissRequest = { genderExpanded = false }
                ) {
                    DropdownMenuItem(onClick = {
                        gender = "Male"
                        genderExpanded = false
                    }) {
                        Text("Male")
                    }
                    DropdownMenuItem(onClick = {
                        gender = "Female"
                        genderExpanded = false
                    }) {
                        Text("Female")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))


            Button(
                onClick = {
                    if (name.isBlank() || username.isBlank()) {
                        Toast.makeText(
                            context,
                            "Name and username cannot be empty",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }
                    if (userId != null) {
                        val updatedUser = mapOf(
                            "name" to name,
                            "username" to username,
                            "gender" to gender,
                            "phone" to phone,
                            "email" to email
                        )

                        db.collection("users").document(userId)
                            .update(updatedUser)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Profile updated successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.popBackStack() // Return to ProfileScreen
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    context,
                                    "Error updating profile: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnCompleteListener {
                                isLoading = false
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF4A5182))
            ) {
                Text("Save", color = Color.White)
            }
        }
    }
}
