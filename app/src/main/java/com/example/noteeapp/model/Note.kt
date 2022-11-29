package com.example.noteeapp.model

import android.net.Uri
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.time.Instant
import java.util.Date

@Entity(tableName = "notes")
@Parcelize
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val noteTitle: String,
    val noteBody: String,
    val noteImagen: String,
    //val noteAudio: String,
    val noteDate: String
) : Parcelable


@Entity(tableName = "task")
@Parcelize
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val taskTitle: String,
    val taskBody: String,
    val initialDate: String,
    val finalDate: String
) : Parcelable
