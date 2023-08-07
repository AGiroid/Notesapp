package com.example.myapplication.room



import androidx.room.*
import com.example.myapplication.dataclass.Note

@Dao
interface NotesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT * FROM notesTable ORDER BY ID ASC")
    fun getAllNotes():List<Note>
}