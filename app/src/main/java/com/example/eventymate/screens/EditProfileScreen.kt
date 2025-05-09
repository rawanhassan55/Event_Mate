package com.example.eventymate.screens

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventymate.ui.theme.ThemeColors

@Composable
fun EditProfileScreen(navController: NavController,isDarkTheme: Boolean,
) {
    var name by remember { mutableStateOf("Rawan Hassan") }
    var username by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Female") }
    var phone by remember { mutableStateOf("+20 1233344002") }
    var email by remember { mutableStateOf("rh045@email.com") }
    var genderExpanded by remember { mutableStateOf(false) }
    val colors = if (isDarkTheme) ThemeColors.Night else ThemeColors.Day


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(colors.secondary)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(colors.secondary)
                .clickable { navController.popBackStack() }
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Edit Profile", fontSize = 20.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
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
