package com.example.eventymate.presentation

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.eventymate.Notification.EventNotificationWorker
import com.example.eventymate.data.Note
import com.example.eventymate.data.NoteDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.TimeUnit

class NotesViewModel(
    private val dao: NoteDao,
    application: Application
) : AndroidViewModel(application) {

    // Theme state management
    private val _isDarkTheme = mutableStateOf(false)
    val isDarkTheme: State<Boolean> = _isDarkTheme

    private val isSortedByDateAdded = MutableStateFlow(true)

    private var notes =
        isSortedByDateAdded.flatMapLatest { sort ->
            if (sort) {
                dao.getNotesOrderdByDateAdded()
            } else {
                dao.getNotesOrderdByTitle()
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val _state = MutableStateFlow(NoteState())
    val state =
        combine(_state, isSortedByDateAdded, notes) { state, isSortedByDateAdded, notes ->
            state.copy(
                notes = notes
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NoteState())

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    private fun scheduleNotification(note: Note) {
        val currentTime = System.currentTimeMillis()
        val eventTime = note.date.time
        val delay = eventTime - currentTime

        Log.d("NotificationDebug", "Attempting to schedule notification for note: ${note.id}")
        Log.d("NotificationDebug", "Current time: $currentTime, Note time: $eventTime, Delay: $delay ms")

        if (eventTime > currentTime) {
            val inputData = workDataOf(
                "NOTE_ID" to note.id,
                "NOTE_TITLE" to note.title
            )

            Log.d("NotificationDebug", "Creating work request with data: $inputData")

            val notificationWork = OneTimeWorkRequestBuilder<EventNotificationWorker>()
                .setInputData(inputData)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag("event_notification_${note.id}")
                .build()

            WorkManager.getInstance(getApplication()).enqueueUniqueWork(
                "event_notification_${note.id}",
                ExistingWorkPolicy.REPLACE,
                notificationWork
            )

            Log.d("NotificationDebug", "Work enqueued with ID: ${notificationWork.id}")
        } else {
            Log.d("NotificationDebug", "Event time is in the past, not scheduling")
        }
    }

    fun onEvent(event: NotesEvent) {
        when (event) {
            is NotesEvent.DeleteNote -> {
                viewModelScope.launch {
                    dao.deleteNote(event.note)
                }
            }

            is NotesEvent.SaveNote -> {
                val note = Note(
                    title = state.value.title.value,
                    description = state.value.description.value,
                    eventDate = state.value.eventDate.value,
                    eventTime = state.value.eventTime.value,
                    location = state.value.location.value,
                    category = state.value.category,
                    dateAdded = System.currentTimeMillis(),
                    date =  Date(System.currentTimeMillis() + 5000)
                )

                viewModelScope.launch {
                    scheduleNotification(note)
                    dao.upsertNote(note)
                }

                _state.update {
                    it.copy(
                        title = mutableStateOf(""),
                        description = mutableStateOf(""),
                        eventDate = mutableStateOf(""),
                        eventTime = mutableStateOf(""),
                        location = mutableStateOf(""),
                        category = "",
                    )
                }
            }

            is NotesEvent.SelectCategory -> {
                _state.value = state.value.copy(category = event.category)
            }

            NotesEvent.SortNotes -> {
                isSortedByDateAdded.value = !isSortedByDateAdded.value
            }

            else -> {}
        }
    }

}