package com.example.teststudent

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.teststudent.database.StudentDatabaseHelper
import com.example.teststudent.data.StudentModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var databaseHelper: StudentDatabaseHelper
    private lateinit var students: MutableList<StudentModel>
    private lateinit var studentAdapter: StudentAdapter
    private var lastDeletedStudent: StudentModel? = null
    private var lastDeletedPosition: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        databaseHelper = StudentDatabaseHelper(this)

        // Initialize RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_students)
        recyclerView.layoutManager = LinearLayoutManager(this)

        students = mutableListOf()

        lifecycleScope.launch {
            students.addAll(databaseHelper.getAllStudents())
            if (students.isEmpty()) {
                addPredefinedStudents()
            }
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
            recyclerView.adapter = studentAdapter
        }
    }

    private suspend fun addPredefinedStudents() = withContext(Dispatchers.IO) {
        val predefinedStudents = listOf(
            StudentModel(studentName = "Nguyễn Văn An", studentId = "SV001"),
            StudentModel(studentName = "Trần Thị Bảo", studentId = "SV002"),
            StudentModel(studentName = "Lê Hoàng Cường", studentId = "SV003"),
            StudentModel(studentName = "Phạm Thị Dung", studentId = "SV004"),
            StudentModel(studentName = "Đỗ Minh Đức", studentId = "SV005"),
            StudentModel(studentName = "Vũ Thị Hoa", studentId = "SV006"),
            StudentModel(studentName = "Hoàng Văn Hải", studentId = "SV007"),
            StudentModel(studentName = "Bùi Thị Hạnh", studentId = "SV008"),
            StudentModel(studentName = "Đinh Văn Hùng", studentId = "SV009"),
            StudentModel(studentName = "Nguyễn Thị Linh", studentId = "SV010"),
            StudentModel(studentName = "Phạm Văn Long", studentId = "SV011"),
            StudentModel(studentName = "Trần Thị Mai", studentId = "SV012"),
            StudentModel(studentName = "Lê Thị Ngọc", studentId = "SV013"),
            StudentModel(studentName = "Vũ Văn Nam", studentId = "SV014"),
            StudentModel(studentName = "Hoàng Thị Phương", studentId = "SV015"),
            StudentModel(studentName = "Đỗ Văn Quân", studentId = "SV016"),
            StudentModel(studentName = "Nguyễn Thị Thu", studentId = "SV017"),
            StudentModel(studentName = "Trần Văn Tài", studentId = "SV018"),
            StudentModel(studentName = "Phạm Thị Tuyết", studentId = "SV019"),
            StudentModel(studentName = "Lê Văn Vũ", studentId = "SV020"),
        )
        for (student in predefinedStudents) {
            databaseHelper.addStudent(student)
        }
        withContext(Dispatchers.Main) {
            students.addAll(predefinedStudents)
            studentAdapter.notifyDataSetChanged()
            Toast.makeText(this@MainActivity, "Predefined students added to database", Toast.LENGTH_SHORT).show()
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
                    val updatedStudent = StudentModel(id = student.id, studentName = name, studentId = id)
                    lifecycleScope.launch {
                        databaseHelper.updateStudent(updatedStudent)
                        students[position] = updatedStudent
                        studentAdapter.notifyItemChanged(position)
                    }
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
                lifecycleScope.launch {
                    databaseHelper.deleteStudent(student)
                    lastDeletedStudent = student
                    lastDeletedPosition = position
                    students.removeAt(position)
                    studentAdapter.notifyItemRemoved(position)
                    showUndoSnackbar()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showUndoSnackbar() {
        val view = findViewById<View>(R.id.main)
        Snackbar.make(view, "Student deleted", Snackbar.LENGTH_LONG)
            .setAction("Undo") {
                lifecycleScope.launch {
                    lastDeletedStudent?.let {
                        databaseHelper.addStudent(it)
                        students.add(lastDeletedPosition!!, it)
                        studentAdapter.notifyItemInserted(lastDeletedPosition!!)
                    }
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
                findViewById<RecyclerView>(R.id.recycler_view_students).visibility = View.GONE
                findViewById<FrameLayout>(R.id.fragment_container).visibility = View.VISIBLE

                val fragment = AddStudentFragment { newStudent ->
                    lifecycleScope.launch {
                        databaseHelper.addStudent(newStudent)
                        students.add(newStudent)
                        studentAdapter.notifyItemInserted(students.size - 1)

                        supportFragmentManager.popBackStack()
                        findViewById<RecyclerView>(R.id.recycler_view_students).visibility = View.VISIBLE
                        findViewById<FrameLayout>(R.id.fragment_container).visibility = View.GONE
                    }
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
                showEditStudentDialog(studentToEdit, position)
                true
            }
            R.id.delete -> {
                deleteStudent(students[position], position)
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }
}

