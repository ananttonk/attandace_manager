package com.example.attendencemanager.Activity

import android.app.Application
import com.example.attendencemanager.model.AttendanceDataBase

class DataBaseApplication : Application() {
    val database by lazy {
        AttendanceDataBase.getDataBase(this)
    }

}