package com.example.teststudent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StudentAdapter(
    private val students: MutableList<StudentModel>,
    private val onEdit: (StudentModel, Int) -> Unit,
    private val onDelete: (StudentModel, Int) -> Unit,
    private val onShowContextMenu: (View, Int) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {
    private var selectedPosition: Int = -1

    fun setSelectedPosition(position: Int) {
        selectedPosition = position
    }

    fun getSelectedPosition(): Int = selectedPosition

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_student_item, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.studentName.text = student.studentName
        holder.studentId.text = student.studentId

        // Handle single click to show the context menu
        holder.itemView.setOnClickListener {
            onShowContextMenu(holder.itemView, position)
        }
    }

    override fun getItemCount(): Int = students.size

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val studentName: TextView = itemView.findViewById(R.id.text_student_name)
        val studentId: TextView = itemView.findViewById(R.id.text_student_id)
    }
}
