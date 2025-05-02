package com.example.eventymate.presentation

import com.example.eventymate.data.Note


sealed interface NotesEvent {
    object SortNotes: NotesEvent

    data class DeleteNote(val note: Note): NotesEvent

    data class SaveNote(
        val title: String,
        val description: String,
        val eventDate : String,
        val eventTime : String,
        val location : String,
    ): NotesEvent

    data class SelectCategory(val category: String) : NotesEvent

}
