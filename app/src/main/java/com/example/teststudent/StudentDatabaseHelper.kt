package com.example.teststudent.database

import android.content.Context
import androidx.room.*
import com.example.teststudent.data.StudentModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Define the Student entity
//@Entity(tableName = "students")
//data class StudentModel(
//    @PrimaryKey(autoGenerate = true) val id: Int = 0,
//    val studentName: String,
//    val studentId: String
//)

// DAO (Data Access Object) for Student
@Dao
interface StudentDao {
    @Insert
    suspend fun addStudent(student: StudentModel)

    @Update
    suspend fun updateStudent(student: StudentModel)

    @Delete
    suspend fun deleteStudent(student: StudentModel)

    @Query("SELECT * FROM students ORDER BY id ASC")
    suspend fun getAllStudents(): List<StudentModel>

    @Query("DELETE FROM students WHERE id = :studentId")
    suspend fun deleteStudentById(studentId: Int)
}

// Room Database class
@Database(entities = [StudentModel::class], version = 1)
abstract class StudentDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao

    companion object {
        @Volatile
        private var INSTANCE: StudentDatabase? = null

        fun getDatabase(context: Context): StudentDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudentDatabase::class.java,
                    "student_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// StudentDatabaseHelper class for interaction
class StudentDatabaseHelper(context: Context) {

    private val database = StudentDatabase.getDatabase(context)
    private val studentDao = database.studentDao()

    suspend fun addStudent(student: StudentModel): Long {
        return withContext(Dispatchers.IO) {
            studentDao.addStudent(student)
            student.id.toLong()
        }
    }

    suspend fun updateStudent(student: StudentModel) {
        withContext(Dispatchers.IO) {
            studentDao.updateStudent(student)
        }
    }

    suspend fun deleteStudent(student: StudentModel) {
        withContext(Dispatchers.IO) {
            studentDao.deleteStudent(student)
        }
    }

    suspend fun getAllStudents(): List<StudentModel> {
        return withContext(Dispatchers.IO) {
            studentDao.getAllStudents()
        }
    }

    suspend fun deleteStudentById(studentId: Int) {
        withContext(Dispatchers.IO) {
            studentDao.deleteStudentById(studentId)
        }
    }
}
