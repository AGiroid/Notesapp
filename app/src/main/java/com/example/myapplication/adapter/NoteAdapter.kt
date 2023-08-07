package com.example.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.dataclass.Note


class NoteAdapter(
    val context: Context,
    val datalist:ArrayList<Note>,
    val noteClickInterface: NoteClickInterface
) : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvtopic = itemView.findViewById<TextView>(R.id.tvNoteTopic)
        val tvdescription = itemView.findViewById<TextView>(R.id.tvNoteDescription)
        val tvtime = itemView.findViewById<TextView>(R.id.tvTimestamp)
        val editbtn = itemView.findViewById<TextView>(R.id.btnEdit)
        val deletebtn = itemView.findViewById<TextView>(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.notlist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvtopic.text = datalist[position].noteTitle
        holder.tvdescription.text = datalist[position].noteDescription
        holder.tvtime.text = datalist[position].timeStamp
        holder.deletebtn.setOnClickListener {
            noteClickInterface.onDeleteIconClick(position,datalist[position])

        }
        holder.editbtn.setOnClickListener {
            noteClickInterface.onEditIconClick(position,datalist[position])

        }
    }
    fun updatelist(newlist:List<Note>){
        datalist.clear()
        datalist.addAll(newlist)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    interface NoteClickInterface {
        fun onEditIconClick(position:Int,note: Note)
        fun onDeleteIconClick(position:Int,note: Note)
    }
}