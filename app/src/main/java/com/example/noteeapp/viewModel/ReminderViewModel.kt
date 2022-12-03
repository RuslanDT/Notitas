package com.example.noteeapp.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteeapp.model.Note
import com.example.noteeapp.model.Reminder
import com.example.noteeapp.repository.ReminderRepository
import kotlinx.coroutines.launch

class ReminderViewModel(app: Application, private val reminderRepository: ReminderRepository) :
    AndroidViewModel(app) {

    fun addReminder(reminder: Reminder) = viewModelScope.launch {
        reminderRepository.addReminder(reminder)
    }

    fun updateReminder(reminder: Reminder) = viewModelScope.launch {
        reminderRepository.updateReminder(reminder)
    }

    fun deleteReminder(reminder: Reminder) = viewModelScope.launch {
        reminderRepository.deleteReminder(reminder)
    }

    fun getAllReminders() = reminderRepository.getAllReminders()

}