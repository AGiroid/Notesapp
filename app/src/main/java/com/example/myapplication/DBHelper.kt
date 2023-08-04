package com.example.myapplication

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.myapplication.dataclass.Note

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) : SQLiteOpenHelper(
    context, DATABASE_NAME, factory, DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase?) {
        val query =
            ("CREATE TABLE $TABLE_NAME ($ID_COL INTEGER PRIMARY KEY, $Topic_COl TEXT,$Description_COL TEXT,$Time_COL TEXT)")
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addNote(Notedata: Note) {
        val values = ContentValues()
        values.put(Topic_COl, Notedata.noteTitle)
        values.put(Description_COL, Notedata.noteDescription)
        values.put(Time_COL, Notedata.timeStamp)
        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }
    fun updateNote(Notedata: Note):Int {
        val db=this.writableDatabase
        val values = ContentValues()
        values.put(Topic_COl, Notedata.noteTitle)
        values.put(Description_COL, Notedata.noteDescription)
        values.put(Time_COL, Notedata.timeStamp)
        val whereClause = "$Time_COL = ?"
        val whereArgs = arrayOf(Notedata.timeStamp.toString())
        val rowsUpdated = db.update(TABLE_NAME, values, whereClause, whereArgs)
        db.close()
        return rowsUpdated

    }
    fun deleteNote(Notedata: Note):Int{
        val db = this.writableDatabase

        val whereClause = "$Time_COL = ?"
        val whereArgs = arrayOf(Notedata.timeStamp)

        val rowsDeleted = db.delete(TABLE_NAME, whereClause, whereArgs)

        db.close()

        return rowsDeleted
    }

    @SuppressLint("Range")
    fun getData(): ArrayList<Note> {
        val dataList = arrayListOf<Note>()

        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndex(ID_COL))
                val topic = cursor.getString(cursor.getColumnIndex(Topic_COl))
                val description = cursor.getString(cursor.getColumnIndex(Description_COL))
                val time = cursor.getString(cursor.getColumnIndex(Time_COL))
                val dataModel = Note(topic, description, time)
                dataList.add(dataModel)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return dataList
    }

    companion object {
        private val DATABASE_NAME = "Notedatabse"
        private val DATABASE_VERSION = 1
        val TABLE_NAME = "Note_table"
        val ID_COL = "id"
        val Topic_COl = "topic"
        val Description_COL = "description"
        val Time_COL = "time"
    }
}