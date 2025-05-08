package com.example.eventymate.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Note(

    val title: String,
    val description: String,
    val eventDate : String,
    val eventTime : String,
    val location : String,
    val dateAdded: Long,
    val date: Date,
    val category: String,
    val isLoved: Boolean = false ,


    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
