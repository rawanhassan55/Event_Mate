package com.example.eventymate.data

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [Note::class],
    version = 3
)
abstract class NotesDatabase: RoomDatabase(){
    abstract val dao: NoteDao
}