package com.example.teststudent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class AddStudentFragment(private val onStudentAdded: (StudentModel) -> Unit) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_student, container, false)

        val inputStudentName = view.findViewById<EditText>(R.id.input_student_name_frag)
        val inputStudentId = view.findViewById<EditText>(R.id.input_student_id_frag)
        val btnSave = view.findViewById<Button>(R.id.btn_save_frag)
        val btnCancel = view.findViewById<Button>(R.id.btn_cancel_frag)

        btnSave.setOnClickListener {
            val studentName = inputStudentName.text.toString()
            val studentId = inputStudentId.text.toString()

            if (studentName.isNotEmpty() && studentId.isNotEmpty()) {
                val newStudent = StudentModel(studentName, studentId)
                onStudentAdded(newStudent) // Add student to the list
                Toast.makeText(requireContext(), "Student added: $studentName", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack() // Close the fragment
            } else {
                Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }
}
