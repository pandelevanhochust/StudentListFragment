package com.example.teststudent

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class StudentDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "students.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "students"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_STUDENT_ID = "student_id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_STUDENT_ID TEXT NOT NULL
            )
        """
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addStudent(student: StudentModel): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, student.studentName)
            put(COLUMN_STUDENT_ID, student.studentId)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun updateStudent(id: Long, student: StudentModel): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, student.studentName)
            put(COLUMN_STUDENT_ID, student.studentId)
        }
        return db.update(TABLE_NAME, values, "$COLUMN_ID=?", arrayOf(id.toString()))
    }

    fun deleteStudent(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id.toString()))
    }

    fun getAllStudents(): List<StudentModel> {
        val db = readableDatabase
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, null)
        val students = mutableListOf<StudentModel>()

        with(cursor) {
            while (moveToNext()) {
                val name = getString(getColumnIndexOrThrow(COLUMN_NAME))
                val studentId = getString(getColumnIndexOrThrow(COLUMN_STUDENT_ID))
                students.add(StudentModel(name, studentId))
            }
            close()
        }
        return students
    }
}
