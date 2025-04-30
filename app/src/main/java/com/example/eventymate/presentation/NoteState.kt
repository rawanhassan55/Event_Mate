package com.example.eventymate.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.eventymate.data.Note

data class NoteState(

    val notes: List<Note> = emptyList(),
    val title: MutableState<String> = mutableStateOf(""),
    val description: MutableState<String> = mutableStateOf(""),
    val eventDate: MutableState<String> = mutableStateOf(""),
    val eventTime: MutableState<String> = mutableStateOf(""),
    val location: MutableState<String> = mutableStateOf("")

)