package com.example.myapplication

import android.app.Dialog
import android.os.Bundle
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

    lateinit var mainDao: NotesDao
    var datalist = arrayListOf<Note>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = NoteDatabase.getDatabase(applicationContext)
        mainDao = database.getNotesDao()
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
//        db = DBHelper(this, null)  (SQLite database)
        val adapter = NoteAdapter(this, datalist, this)
        val topic = Add_Note_Dialog.findViewById<EditText>(R.id.edTopic)
        val description = Add_Note_Dialog.findViewById<EditText>(R.id.edDescription)
        val btnadd = Add_Note_Dialog.findViewById<Button>(R.id.btnAdd)



        mainBinding.btnAddNote.setOnClickListener {
            Add_Note_Dialog.show()
            topic.text.clear()
            description.text.clear()
        }
//        datalist = db.getData() (get data sqlite)
        mainBinding.rvNotelist.adapter = NoteAdapter(this@MainActivity, datalist, this)
        mainDao.getAllNotes().observe(this) { list ->
            list?.let { adapter.updatelist(it) }
        }
        btnadd.setOnClickListener {

            if (topic.text.isEmpty()) {
                Toast.makeText(this@MainActivity, getString(R.string.topic), Toast.LENGTH_SHORT)
                    .show()
            } else if (description.text.isEmpty()) {
                Toast.makeText(
                    this@MainActivity, getString(R.string.description), Toast.LENGTH_SHORT
                ).show()
            } else {
                if (topic.text.isNotEmpty() && description.text.isNotEmpty()) {
                    val sdf = SimpleDateFormat(getString(R.string.timeformate))
                    val currentDate: String = sdf.format(Date())
                    val note = Note(topic.text.toString(), description.text.toString(), currentDate)
                    lifecycleScope.launch {
                        mainDao.insert(note)
                        datalist.add(note)
                        mainBinding.rvNotelist.adapter?.notifyDataSetChanged()
                    }
//                    db.addNote(note)(Insert)

                    Toast.makeText(this, getString(R.string.inserted), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show()
                }


            }
            Add_Note_Dialog.dismiss()

        }

    }

    override fun onEditIconClick(position: Int, note: Note) {
        val edittopic = Edit_Note_Dialog.findViewById<EditText>(R.id.edTopic)
        val editdescription = Edit_Note_Dialog.findViewById<EditText>(R.id.edDescription)
        val btnedit = Edit_Note_Dialog.findViewById<Button>(R.id.btnEdit)
        Edit_Note_Dialog.show()
        btnedit.setOnClickListener {
            if (edittopic.text.isEmpty()) {
                Toast.makeText(this@MainActivity, getString(R.string.topic), Toast.LENGTH_SHORT)
                    .show()
            } else if (editdescription.text.isEmpty()) {
                Toast.makeText(
                    this@MainActivity, getString(R.string.description), Toast.LENGTH_SHORT
                ).show()
            } else {
                if (edittopic.text.isNotEmpty() && editdescription.text.isNotEmpty()) {
                    val sdf = SimpleDateFormat(getString(R.string.timeformate))
                    val currentDate: String = sdf.format(Date())
                    val note = Note(
                        edittopic.text.toString(),
                        editdescription.text.toString(),
                        currentDate
                    )
                    lifecycleScope.launch {
                        mainDao.update(note)
                        datalist[position] = note
                        mainBinding.rvNotelist.adapter?.notifyItemChanged(position)
                    }
//                    db.updateNote(note)(update)

                    Toast.makeText(this, getString(R.string.note_update), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show()
                }


            }
            Edit_Note_Dialog.dismiss()
        }
    }

    override fun onDeleteIconClick(position: Int, note: Note) {
//        db.deleteNote(note)(Delete)
        lifecycleScope.launch {
            mainDao.delete(note)
            datalist.removeAt(position)
            mainBinding.rvNotelist.adapter?.notifyItemRemoved(position)

        }
        Toast.makeText(this, getString(R.string.Remove), Toast.LENGTH_SHORT).show()
    }


}