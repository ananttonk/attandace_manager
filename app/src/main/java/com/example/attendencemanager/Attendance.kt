package com.example.attendencemanager

import com.example.attendencemanager.model.RollStudentEntity

interface Attendance {
    fun setAttendance(model: RollStudentEntity, isPresent: Boolean)
}