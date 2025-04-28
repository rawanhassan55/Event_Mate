package com.example.eventymate

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.work.WorkManager
import com.example.eventymate.auth.AuthViewModel
import com.example.eventymate.ui.theme.EventyMateTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase with explicit options
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setProjectId("event-mate-1352d")
                    .setApplicationId("1:717443359636:android:45bfd850580688089ce581")
                    .setApiKey("AIzaSyDs-kbAJ9USPtpnluQsL9aWRYPcmV7W4kM")
                    .build()
                FirebaseApp.initializeApp(this, options)
            }
        } catch (e: Exception) {
            Log.e("Firebase", "Error initializing Firebase: ${e.message}")
        }

        val workManager = WorkManager.getInstance(this)
        Log.d("NotificationDebug", "WorkManager initialized: $workManager")


        setContent {
            EventyMateTheme {
            }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authViewModel = AuthViewModel(application)
                    EventNavigation(
                        authViewModel = authViewModel,
                        eventViewModel = authViewModel
                    )
                }
            }
        }
//        requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
//        createNotificationChannel()
    }

//    private fun createNotificationChannel() {
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                "event_countdown_channel",
//                "Event Countdown Notifications",
//                NotificationManager.IMPORTANCE_HIGH
//            ).apply {
//                description = "Notifications for when events are happening"
//                enableLights(true)
//                enableVibration(true)
//            }
//
//            val notificationManager =
//                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//    }

