package com.example.myapplication.dataclass

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "notesTable")
data class Note(
    @PrimaryKey(autoGenerate = false) val id:String,
    @ColumnInfo(name = "title")val noteTitle:String,
    @ColumnInfo(name = "Description")val noteDescription:String,
    @ColumnInfo(name = "Timestamp")val timeStamp:String)

