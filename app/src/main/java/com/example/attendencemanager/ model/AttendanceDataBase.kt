package com.example.attendencemanager.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [SubClassEntity::class,RollStudentEntity::class,DateEntity::class], version = 2)
abstract class AttendanceDataBase : RoomDatabase() {

    abstract fun subClassDao(): SubClassDao
    abstract fun rollStudentDao():RollStudentDao
    abstract fun dateDao():DateDao

    companion object {
        @Volatile
        private var INSTANCE: AttendanceDataBase? = null

        fun getDataBase(context: Context): AttendanceDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AttendanceDataBase::class.java,
                    "sub_Class_DataBase"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}