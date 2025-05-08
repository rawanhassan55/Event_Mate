package com.example.eventymate.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventymate.data.Note
import com.example.eventymate.presentation.NoteState
import com.example.eventymate.presentation.NotesEvent
import com.example.eventymate.presentation.NotesViewModel
import com.example.eventymate.ui.theme.ThemeColors
import androidx.compose.material.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController


@Composable
fun LovedScreen(
    state: NoteState,
    viewModel: NotesViewModel,
    isDarkTheme: Boolean,
    navController: NavController
) {
    val lovedNotes = state.notes.filter { it.isLoved }
    val colors = if (isDarkTheme) ThemeColors.Night else ThemeColors.Day

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Loved Notes ❤️", color = colors.text) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = colors.text
                        )
                    }
                },
                backgroundColor = colors.surafce,
                elevation = 4.dp
            )
        },
        backgroundColor = colors.bacground
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (lovedNotes.isEmpty()) {
                item {
                    Text(
                        "No loved notes yet.",
                        color = colors.text,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp
                    )
                }
            } else {
                items(lovedNotes.size) { index ->
                    NoteItem(
                        note = lovedNotes[index],
                        onEvent = viewModel::onEvent,
                        containerColor = colors.surafce,
                        textColor = colors.text,
                        isDarkTheme = isDarkTheme
                    )
                }
            }
        }
    }
}






@Composable
fun NoteItem(
    note: Note,
    onEvent: (NotesEvent) -> Unit,
    containerColor: Color,
    textColor: Color,
    isDarkTheme: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(containerColor)
            .padding(12.dp)
    ) {
        Row {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = note.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = note.description,
                    fontSize = 18.sp,
                    color = textColor
                )
            }
            IconButton(
                onClick = {
                    onEvent(NotesEvent.DeleteNote(note))
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Delete Note",
                    modifier = Modifier.size(35.dp),
                    tint = textColor
                )
            }
            // Add Love Icon Button
            IconButton(
                onClick = {
                    onEvent(NotesEvent.ToggleLoveNote(note))
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Love Note",
                    tint = if (note.isLoved) Color.Red else textColor,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "\uD83D\uDCC5 ${note.eventDate}",
                fontSize = 14.sp,
                color = textColor,
                modifier = Modifier.weight(2f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = note.eventTime,
                fontSize = 16.sp,
                color = textColor,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "\uD83D\uDCCD${note.location}",
                fontSize = 16.sp,
                color = textColor,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = note.category,
            fontSize = 16.sp,
            fontStyle = FontStyle.Italic,
            color = if (isDarkTheme) Color.Cyan else Color.Blue
        )
    }
}