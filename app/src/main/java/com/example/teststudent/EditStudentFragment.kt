package com.example.teststudent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.teststudent.database.StudentDatabaseHelper
import com.example.teststudent.data.StudentModel
import kotlinx.coroutines.launch

class EditStudentFragment : Fragment() {

    private lateinit var databaseHelper: StudentDatabaseHelper
    private var studentToEdit: StudentModel? = null
    private var onStudentUpdated: ((StudentModel) -> Unit)? = null

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
        databaseHelper = StudentDatabaseHelper(requireContext())

        val inputStudentName = view.findViewById<EditText>(R.id.input_student_name_frag_edit)
        val inputStudentId = view.findViewById<EditText>(R.id.input_student_id_frag_edit)
        val btnSave = view.findViewById<Button>(R.id.btn_save_frag_edit)
        val btnCancel = view.findViewById<Button>(R.id.btn_cancel_frag_edit)

        studentToEdit?.let {
            inputStudentName.setText(it.studentName)
            inputStudentId.setText(it.studentId)
        }

        btnSave.setOnClickListener {
            val updatedName = inputStudentName.text.toString()
            val updatedId = inputStudentId.text.toString()

            if (updatedName.isNotEmpty() && updatedId.isNotEmpty()) {
                val updatedStudent = studentToEdit?.copy(studentName = updatedName, studentId = updatedId)
                lifecycleScope.launch {
                    if (updatedStudent != null) {
                        databaseHelper.updateStudent(updatedStudent)
                        onStudentUpdated?.invoke(updatedStudent) // Notify parent activity or fragment
                        Toast.makeText(requireContext(), "Student updated: $updatedName", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack() // Close the fragment
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            parentFragmentManager.popBackStack() // Close the fragment
        }

        return view
    }
}

