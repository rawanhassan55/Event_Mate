package com.example.eventymate.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Note::class], version = 5)
@TypeConverters(Converters::class)
abstract class NotesDatabase: RoomDatabase(){
    abstract val dao: NoteDao
}