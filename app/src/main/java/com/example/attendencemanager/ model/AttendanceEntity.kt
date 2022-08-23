package com.example.attendencemanager.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Class_and_subject")
data class SubClassEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo val ClassName: String = "",
    @ColumnInfo val SubjectName: String = "",
)

@Entity(
    tableName = "rollNumber_and_student",
)
data class RollStudentEntity(
    @PrimaryKey(autoGenerate = true)
    var idR: Int,
    @ColumnInfo val Name: String = "",
    @ColumnInfo val RollNumber: Int,
    @ColumnInfo var classId: Int,
    var isAttendance: Boolean = false
)

@Entity(tableName = "attendance")
data class DateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo val date: String,
    @ColumnInfo var studentId: Int,
    @ColumnInfo var studentName: String,
    @ColumnInfo var classId: Int,
    @ColumnInfo var rollNumber:Int
)
