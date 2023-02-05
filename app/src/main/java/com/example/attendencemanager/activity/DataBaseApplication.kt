package com.example.attendencemanager.activity

import android.app.Application
import com.example.attendencemanager.model.AttendanceDataBase

class DataBaseApplication : Application() {
    val database by lazy {
        AttendanceDataBase.getDataBase(this)
    }

}