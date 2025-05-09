package com.vladosik0.schedulerapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Task::class],
    version = 1,
    exportSchema = false
)
abstract class SchedulerDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var Instance: SchedulerDatabase? = null
        fun getDatabase(context: Context): SchedulerDatabase{
            return Instance ?: synchronized(this){
                Room.databaseBuilder(context, SchedulerDatabase::class.java, "scheduler_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}