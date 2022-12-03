package com.example.noteeapp.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel

import androidx.lifecycle.ViewModelProvider
import com.example.noteeapp.repository.ReminderRepository

class ReminderViewModelProviderFactory(
    val app: Application,
    private val reminderRepository: ReminderRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ReminderViewModel(app, reminderRepository) as T
    }
}