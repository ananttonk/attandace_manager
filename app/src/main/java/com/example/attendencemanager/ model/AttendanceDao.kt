package com.example.attendencemanager.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SubClassDao {

    @Insert
    suspend fun insertSubClass(subClass: SubClassEntity)

    @Delete
    suspend fun deleteSubClass(subClass: SubClassEntity)

    @Update
    suspend fun updateSubClass(subClass: SubClassEntity)

    @Query("SELECT * FROM `Class_and_subject` WHERE id=:id")
    fun getUpdateSubClass(id: Int): Flow<SubClassEntity>

    @Query("SELECT * FROM Class_and_subject ORDER BY ID")
    fun getAllSubClassList(): Flow<List<SubClassEntity>>

    @Query("SELECT * FROM `Class_and_subject` WHERE id=:id")
    fun getDeleteSubClass(id: Int): Flow<SubClassEntity>
}

@Dao
interface RollStudentDao {
    @Insert
    suspend fun insertRollStudent(nameClass: RollStudentEntity)

    @Query("DELETE  FROM rollNumber_and_student WHERE idR=:id")
    suspend fun deleteStudent(id: Int)

    @Query("UPDATE rollNumber_and_student set Name=:studentName,rollNumber=:rollNumber WHERE idR=:id")
    suspend fun updateStudentName(id: Int,studentName:String,rollNumber: Int)

    @Query("SELECT * FROM `rollNumber_and_student` WHERE idR=:id")
    fun getUpdateStudent(id: Int): Flow<RollStudentEntity>

    @Query("SELECT * FROM `rollNumber_and_student` WHERE idR=:id")
    fun getDeleteStudent(id: Int): Flow<RollStudentEntity>

    @Query("DELETE  FROM rollNumber_and_student WHERE classId=:classId")
    suspend fun deleteStudentWhenDeleteClass(classId: Int)

    @Query("SELECT * FROM rollNumber_and_student where classId=:classId ORDER BY RollNumber ASC")
    fun getStudentList(classId: Int): Flow<MutableList<RollStudentEntity>>

}

@Dao
interface DateDao {
    @Insert
    suspend fun insertDate(date: DateEntity)

    @Query("SELECT * FROM attendance WHERE date=:date and classId=:classId")
    fun checkAttendance(date: String, classId: Int): List<DateEntity>

    @Delete
    suspend fun deleteAttendance(dateEntity: DateEntity)

    @Query("SELECT * FROM attendance WHERE date=:date AND classId=:classId ORDER BY rollNumber")
    fun attendance(date: String, classId: Int): Flow<List<DateEntity>>

    @Query("SELECT * From attendance WHERE classId=:classId AND RollNumber=:rollNumber")
    fun allStudentDetails(classId: Int, rollNumber: Int): Flow<List<DateEntity>>

    @Query("SELECT * From attendance WHERE classId=:classId AND RollNumber=:rollNumber")
    fun studentDetails(classId: Int, rollNumber: Int): Flow<DateEntity>

    @Query("UPDATE attendance set studentName=:studentName,rollNumber=:rollNumber WHERE classId=:classId")
    suspend fun updateStudentNameWhenStudentNameUpdate(classId: Int,studentName:String,rollNumber: Int)

    @Query("DELETE  FROM attendance WHERE classId=:classId")
    suspend fun deleteStudentAttendanceWhenDeleteClass(classId: Int)

    @Query("DELETE  FROM attendance WHERE studentId=:studentId")
    suspend fun deleteStudentAttendanceWhenDeleteStudent(studentId: Int)

}