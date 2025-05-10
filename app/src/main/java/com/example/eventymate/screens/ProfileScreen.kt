package com.example.eventymate.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.eventymate.R
import com.example.eventymate.ui.theme.ThemeColors
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun ProfileScreen(
    navController: NavController,
    onSignOut: () -> Unit,
    isDarkTheme: Boolean,
) {
    val colors = if (isDarkTheme) ThemeColors.Night else ThemeColors.Day
    val context = LocalContext.current
    val sharedPref =
        remember { context.getSharedPreferences("profile_prefs", Context.MODE_PRIVATE) }

    val nameState = remember { mutableStateOf("") }

    //get the username from firebase
    LaunchedEffect(Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        nameState.value = document.getString("name") ?: "User"
                    }
                }
                .addOnFailureListener {
                    nameState.value = "Failed to load name"
                }
        }
    }
    // Load saved URI if available
    var imageUri by remember {
        mutableStateOf<Uri?>(
            sharedPref.getString("profile_image_uri", null)?.let { Uri.parse(it) }
        )
    }

    // Launcher for image picker
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            // Save the URI to SharedPreferences
            sharedPref.edit().putString("profile_image_uri", it.toString()).apply()
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launcher.launch("image/*")
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.secondary)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))


        Box {
            Image(
                painter = imageUri?.let { rememberAsyncImagePainter(it) }
                    ?: painterResource(id = R.drawable.person_add),
                contentDescription = "Profile",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(colors.secondary)
                    .border(2.dp, Color.Gray, CircleShape)
            )
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = colors.text,
                modifier = Modifier
                    .size(24.dp)
                    .background(Color.Gray, CircleShape)
                    .align(Alignment.BottomEnd)
                    .clickable {
                        launcher.launch("image/*") // No permission check needed
                    }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "${nameState.value}", fontSize = 20.sp, fontWeight = FontWeight.Bold,
            color = colors.text
        )

        Spacer(modifier = Modifier.height(32.dp))


        ProfileItem(
            "Edit Profile",
            R.drawable.edit_icon,
            isDarkTheme,
        ) { navController.navigate("edit_profile") }
        ProfileItem(
            "Notification", R.drawable.baseline_notifications,
            isDarkTheme
        ) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        }

        ProfileItem(
            "Change Password", R.drawable.lock,
            isDarkTheme
        ) {navController.navigate("forgot_password") }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onSignOut() },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF4A5182))
        ) {
            Text("Sign Out", color = Color.White)
        }
    }
}

@Composable
fun ProfileItem(
    title: String,
    iconResId: Int,
    isDarkTheme: Boolean,
    onClick: () -> Unit,
) {
    val colors = if (isDarkTheme) ThemeColors.Night else ThemeColors.Day
    val icon = painterResource(id = iconResId)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painter = icon, contentDescription = null, tint = Color.Gray)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            title, fontSize = 16.sp,
            color = colors.text
        )
    }
}

