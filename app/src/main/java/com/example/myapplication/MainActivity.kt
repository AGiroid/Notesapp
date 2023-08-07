package com.example.myapplication

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.adapter.NoteAdapter
import com.example.myapplication.database.NoteDatabase
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.dataclass.Note
import com.example.myapplication.room.NotesDao
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), NoteAdapter.NoteClickInterface {
    private val mainBinding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_main)
    }
    private val Add_Note_Dialog: Dialog by lazy {
        Dialog(this@MainActivity, R.style.DialogCustomTheme)
    }
    private val Edit_Note_Dialog: Dialog by lazy {
        Dialog(this@MainActivity, R.style.DialogCustomTheme)
    }

    //    lateinit var db: DBHelper
    var datalist = arrayListOf<Note>()
    lateinit var db: NotesDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = NoteDatabase.getDatabase(this).getNotesDao()
        datalist.addAll(db.getAllNotes())
        Add_Note_Dialog.setContentView(R.layout.add_dialog)
        Add_Note_Dialog.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        Edit_Note_Dialog.setContentView(R.layout.edit_dialog)
        Edit_Note_Dialog.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
//        db = DBHelper(this, null)
        val topic = Add_Note_Dialog.findViewById<EditText>(R.id.edTopic)
        val description = Add_Note_Dialog.findViewById<EditText>(R.id.edDescription)
        val btnAdd = Add_Note_Dialog.findViewById<Button>(R.id.btnAdd)

        mainBinding.btnAddNote.setOnClickListener {
            Add_Note_Dialog.show()
            topic.text.clear()
            description.text.clear()
        }


//        datalist = db.getData()

        mainBinding.rvNotelist.adapter = NoteAdapter(this@MainActivity, datalist, this)
        btnAdd.setOnClickListener {
            if (topic.text.isEmpty()) {
                Toast.makeText(this@MainActivity, "Please enter the topic", Toast.LENGTH_SHORT)
                    .show()
            } else if (description.text.isEmpty()) {
                Toast.makeText(
                    this@MainActivity,
                    "Please enter the Description",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (topic.text.isNotEmpty() && description.text.isNotEmpty()) {
                    val sdf = SimpleDateFormat(getString(R.string.timeformate))
                    val currentDate: String = sdf.format(Date())
                    val note = Note(
                        UUID.randomUUID().toString(),
                        topic.text.toString(),
                        description.text.toString(),
                        currentDate
                    )
//                    db.addNote(note)
                    lifecycleScope.launch { db.insert(note) }

                    datalist.add(note)
                    mainBinding.rvNotelist.adapter!!.notifyDataSetChanged()
                    Toast.makeText(this, getString(R.string.inserted), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show()
                }


            }
            Add_Note_Dialog.dismiss()

        }
    }

    override fun onEditIconClick(position: Int, note: Note) {
//        db.updateNote(note)
        Edit_Note_Dialog.show()
        val modify_topic = Edit_Note_Dialog.findViewById<EditText>(R.id.edTopic)
        val modify_description = Edit_Note_Dialog.findViewById<EditText>(R.id.edDescription)
        val btnUpdate = Edit_Note_Dialog.findViewById<Button>(R.id.btnUpdate)
        modify_topic.setText(note.noteTitle)
        modify_description.setText(note.noteDescription)
        btnUpdate.setOnClickListener {
            if (modify_topic.text.isEmpty()) {
                Toast.makeText(this@MainActivity, "Please enter the topic", Toast.LENGTH_SHORT)
                    .show()
            } else if (modify_description.text.isEmpty()) {
                Toast.makeText(
                    this@MainActivity,
                    "Please enter the Description",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val sdf = SimpleDateFormat(getString(R.string.timeformate))
                val currentDate: String = sdf.format(Date())
                val modifynote = Note(
                    note.id,
                    modify_topic.text.toString(),
                    modify_description.text.toString(),
                    currentDate
                )
                lifecycleScope.launch { db.update(modifynote) }
                datalist[position] = modifynote
                mainBinding.rvNotelist.adapter!!.notifyItemChanged(position)
                Toast.makeText(this, getString(R.string.note_update), Toast.LENGTH_SHORT).show()
            }
            Edit_Note_Dialog.dismiss()
        }


    }

    override fun onDeleteIconClick(position: Int, note: Note) {
//        db.deleteNote(note)
        lifecycleScope.launch {
            db.delete(note)
        }
        datalist.removeAt(position)
        mainBinding.rvNotelist.adapter!!.notifyItemRemoved(position)

        Log.d("error", note.noteDescription)
    }
}