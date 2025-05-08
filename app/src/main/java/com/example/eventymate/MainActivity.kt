package com.example.eventymate

import android.app.NotificationChannel
import androidx.work.OneTimeWorkRequestBuilder
import java.util.concurrent.TimeUnit
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkManager
import com.example.eventymate.auth.AuthViewModel
import com.example.eventymate.ui.theme.EventyMateTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getSystemService
import androidx.room.Room
import androidx.work.workDataOf
import com.example.eventymate.Notification.EventNotificationWorker
import com.example.eventymate.data.NotesDatabase
import com.example.eventymate.locale.LocaleHelper
import com.example.eventymate.navigation.EventNavigation
import com.example.eventymate.presentation.NotesViewModel

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(
            LocaleHelper.setLocale(
                newBase,
                LocaleHelper.getPersistedLanguage(newBase)
            )
        )
    }

    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            NotesDatabase::class.java,
            "notes.db"
        )
           //.fallbackToDestructiveMigration() // to reset Database
            .build()
    }

    private val viewModel by viewModels<NotesViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NotesViewModel(
                        application = application,
                        dao = database.dao
                    ) as T
                }
            }
        }
    )

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

        //Notification Manager
        val workManager = WorkManager.getInstance(this)
        Log.d("NotificationDebug", "WorkManager initialized: $workManager")

        var currentLanguage by mutableStateOf(LocaleHelper.getPersistedLanguage(this))

        setContent {
            val context = LocalContext.current

            EventyMateTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authViewModel = AuthViewModel(application)
                    val state by viewModel.state.collectAsState()
                    val navController = rememberNavController()

                    EventNavigation(
                        authViewModel = authViewModel,
                        state = state,
                        viewModel = viewModel,
                        onLanguageToggle = { newLang ->
                            LocaleHelper.persistLanguage(this, newLang)
                            recreate() // Restart to apply the locale change
                        }
                    )
                }
            }
        }

        requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
        createNotificationChannel()
    }


    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "event_mate_channel",
                "Event Countdown Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for when events are happening"
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

