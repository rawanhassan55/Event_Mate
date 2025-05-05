package com.example.eventymate.Notification

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.eventymate.data.NoteDao
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.eventymate.Notification.EventNotificationWorker
import com.example.eventymate.data.Note
import java.util.concurrent.TimeUnit

class NotificationViewModel(
    application: Application,
    private val noteDao: NoteDao,
) : AndroidViewModel(application) {



    private fun cancelExistingNotification(eventId: Int) {
        WorkManager.getInstance(getApplication())
            .cancelUniqueWork("event_notification_$eventId")
    }
}