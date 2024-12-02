package com.example.teststudent

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import android.widget.FrameLayout
import androidx.fragment.app.Fragment

import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var students: MutableList<StudentModel>
    private lateinit var studentAdapter: StudentAdapter
    private var lastDeletedStudent: StudentModel? = null
    private var lastDeletedPosition: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        students = mutableListOf(
            StudentModel("Nguyễn Văn An", "SV001"),
            StudentModel("Trần Thị Bảo", "SV002"),
            StudentModel("Lê Hoàng Cường", "SV003"),
            StudentModel("Phạm Thị Dung", "SV004"),
            StudentModel("Đỗ Minh Đức", "SV005"),
            StudentModel("Vũ Thị Hoa", "SV006"),
            StudentModel("Hoàng Văn Hải", "SV007"),
            StudentModel("Bùi Thị Hạnh", "SV008"),
            StudentModel("Đinh Văn Hùng", "SV009"),
            StudentModel("Nguyễn Thị Linh", "SV010"),
            StudentModel("Phạm Văn Long", "SV011"),
            StudentModel("Trần Thị Mai", "SV012"),
            StudentModel("Lê Thị Ngọc", "SV013"),
            StudentModel("Vũ Văn Nam", "SV014"),
            StudentModel("Hoàng Thị Phương", "SV015"),
            StudentModel("Đỗ Văn Quân", "SV016"),
            StudentModel("Nguyễn Thị Thu", "SV017"),
            StudentModel("Trần Văn Tài", "SV018"),
            StudentModel("Phạm Thị Tuyết", "SV019"),
            StudentModel("Lê Văn Vũ", "SV020")
        )

        studentAdapter = StudentAdapter(
            students,
            onEdit = { student, position -> showEditStudentDialog(student, position) },
            onDelete = { student, position -> deleteStudent(student, position) },
            onShowContextMenu = { view, position ->
                studentAdapter.setSelectedPosition(position)
                registerForContextMenu(view)
                openContextMenu(view)
                unregisterForContextMenu(view)
            }
        )

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_students)
        recyclerView.run {
            adapter = studentAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun showEditStudentDialog(student: StudentModel, position: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_student, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.input_student_name)
        val idInput = dialogView.findViewById<EditText>(R.id.input_student_id)

        nameInput.setText(student.studentName)
        idInput.setText(student.studentId)

        AlertDialog.Builder(this)
            .setTitle("Edit Student")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = nameInput.text.toString()
                val id = idInput.text.toString()
                if (name.isNotEmpty() && id.isNotEmpty()) {
                    students[position] = StudentModel(name, id)
                    studentAdapter.notifyItemChanged(position)
                } else {
                    Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteStudent(student: StudentModel, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Deletion")
            .setMessage("Are you sure you want to delete ${student.studentName}?")
            .setPositiveButton("Delete") { _, _ ->
                lastDeletedStudent = student
                lastDeletedPosition = position
                students.removeAt(position)
                studentAdapter.notifyItemRemoved(position)
                showUndoSnackbar()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showUndoSnackbar() {
        val view = findViewById<View>(R.id.main)
        Snackbar.make(view, "Student deleted", Snackbar.LENGTH_LONG)
            .setAction("Undo") {
                lastDeletedStudent?.let {
                    students.add(lastDeletedPosition!!, it)
                    studentAdapter.notifyItemInserted(lastDeletedPosition!!)
                }
            }
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_student -> {
//                val dialogView = layoutInflater.inflate(R.layout.dialog_add_edit_student, null)
//                // Get references to the input fields
//                val inputStudentName = dialogView.findViewById<EditText>(R.id.input_student_name)
//                val inputStudentId = dialogView.findViewById<EditText>(R.id.input_student_id)
//
//                val builder = AlertDialog.Builder(this)
//                builder.setTitle("Add Student")
//                builder.setView(dialogView)
//
//                builder.setPositiveButton("Add") { dialog, _ ->
//                    val studentName = inputStudentName.text.toString()
//                    val studentId = inputStudentId.text.toString()
//
//                    if (studentName.isNotEmpty() && studentId.isNotEmpty()) {
//                        val newStudent = StudentModel(studentName, studentId)
//                        students.add(newStudent)
//                        studentAdapter.notifyItemInserted(students.size - 1)
//                        Toast.makeText(this, "Student added: $studentName ($studentId)", Toast.LENGTH_SHORT).show()
//                    } else {
//                        Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
//                    }
//                    dialog.dismiss()
//                }
//                builder.setNegativeButton("Cancel") { dialog, _ ->
//                    dialog.cancel()
//                }
//                builder.show()

                findViewById<RecyclerView>(R.id.recycler_view_students).visibility = View.GONE
                findViewById<FrameLayout>(R.id.fragment_container).visibility = View.VISIBLE

                val fragment = AddStudentFragment { newStudent ->
                    students.add(newStudent)
                    studentAdapter.notifyItemInserted(students.size - 1)

                    supportFragmentManager.popBackStack()
                    findViewById<RecyclerView>(R.id.recycler_view_students).visibility = View.VISIBLE
                    findViewById<FrameLayout>(R.id.fragment_container).visibility = View.GONE
                }
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = studentAdapter.getSelectedPosition()
        return when (item.itemId) {
            R.id.edit -> {
                val studentToEdit = students[position]
                val fragment = EditStudentFragment.newInstance(
                    studentToEdit = studentToEdit,
                    onStudentUpdated = { updatedStudent ->
                        students[position] = updatedStudent
                        studentAdapter.notifyItemChanged(position)
                        findViewById<RecyclerView>(R.id.recycler_view_students).visibility = View.VISIBLE
                        findViewById<FrameLayout>(R.id.fragment_container).visibility = View.GONE
                    }
                )
                openFragment(fragment)
                true
            }

            R.id.delete -> {
                deleteStudent(students[position], position)
                true
            }

            else -> super.onContextItemSelected(item)
        }
    }

    private fun openFragment(fragment: Fragment) {
        findViewById<RecyclerView>(R.id.recycler_view_students).visibility = View.GONE
        findViewById<FrameLayout>(R.id.fragment_container).visibility = View.VISIBLE

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}