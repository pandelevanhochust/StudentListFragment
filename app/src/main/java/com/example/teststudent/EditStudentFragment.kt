package com.example.teststudent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class EditStudentFragment : Fragment() {

    private var onStudentUpdated: ((StudentModel) -> Unit)? = null
    private var studentToEdit: StudentModel? = null

    companion object {
        fun newInstance(
            studentToEdit: StudentModel,
            onStudentUpdated: (StudentModel) -> Unit
        ): EditStudentFragment {
            val fragment = EditStudentFragment()
            fragment.studentToEdit = studentToEdit
            fragment.onStudentUpdated = onStudentUpdated
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_student, container, false)

        val inputStudentName = view.findViewById<EditText>(R.id.input_student_name_frag_edit)
        val inputStudentId = view.findViewById<EditText>(R.id.input_student_id_frag_edit)
        val btnSave = view.findViewById<Button>(R.id.btn_save_frag_edit)
        val btnCancel = view.findViewById<Button>(R.id.btn_cancel_frag_edit)

        // Pre-fill the fields with the current student data
        studentToEdit?.let {
            inputStudentName.setText(it.studentName)
            inputStudentId.setText(it.studentId)
        }

        // Handle the "Save" button click
        btnSave.setOnClickListener {
            val updatedName = inputStudentName.text.toString()
            val updatedId = inputStudentId.text.toString()

            if (updatedName.isNotEmpty() && updatedId.isNotEmpty()) {
                val updatedStudent = StudentModel(updatedName, updatedId)
                onStudentUpdated?.invoke(updatedStudent) // Notify the activity
                parentFragmentManager.popBackStack() // Close the fragment
            } else {
                Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle the "Cancel" button click
        btnCancel.setOnClickListener {
            parentFragmentManager.popBackStack() // Close the fragment
        }

        return view
    }
}
