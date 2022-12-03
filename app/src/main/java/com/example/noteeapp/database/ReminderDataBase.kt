package com.example.noteeapp.database

import android.content.Context
import androidx.room.Room
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.noteeapp.model.Reminder

@Database(entities = [Reminder::class], version = 2)
abstract class ReminderDataBase : RoomDatabase() {

    abstract fun getReminderDao(): ReminderDao

    companion object {

        @Volatile
        private var INSTANCE: ReminderDataBase? = null

        fun getInstance(context: Context): ReminderDataBase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ReminderDataBase::class.java,
                        "reminder_db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}