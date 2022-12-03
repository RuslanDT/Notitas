package com.example.noteeapp.repository

import com.example.noteeapp.database.ReminderDataBase
import com.example.noteeapp.database.TaskDataBase
import com.example.noteeapp.model.Reminder
import com.example.noteeapp.model.Task

class ReminderRepository(private val db: ReminderDataBase) {

    suspend fun addReminder(reminder: Reminder) = db.getReminderDao().addReminder(reminder)
    suspend fun updateReminder(reminder: Reminder) = db.getReminderDao().updateReminder(reminder)
    suspend fun deleteReminder(reminder: Reminder) = db.getReminderDao().deleteReminder(reminder)
    fun getAllReminders() = db.getReminderDao().getAllReminders()

}