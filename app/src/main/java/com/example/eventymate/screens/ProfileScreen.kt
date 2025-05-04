package com.example.eventymate.screens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventymate.R
import com.example.eventymate.ui.theme.ThemeColors


@Composable
fun ProfileScreen(
    navController: NavController,
    onSignOut: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ThemeColors.Night.surafce)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))


        Box {
            Image(
                painter = painterResource(id = R.drawable.person_add),
                contentDescription = "Profile",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
            )
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .background(Color.Gray, CircleShape)
                    .align(Alignment.BottomEnd)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Rawan Hassan", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(32.dp))


        ProfileItem("Edit Profile", R.drawable.edit_icon) { navController.navigate("edit_profile") }
        ProfileItem("Notification", R.drawable.baseline_notifications) { }
        ProfileItem("Change Password", R.drawable.lock) { }

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
    onClick: () -> Unit,
) {
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
        Text(title, fontSize = 16.sp)
    }
}

