package com.example.noteeapp.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteeapp.model.Task
import com.example.noteeapp.repository.NoteRepository
import com.example.noteeapp.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(app: Application, private val taskRepository: TaskRepository) :
    AndroidViewModel(app) {

    fun addTask(task: Task) = viewModelScope.launch {
        taskRepository.addTask(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        taskRepository.updateTask(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        taskRepository.deleteTask(task)
    }

    fun getAllTasks() = taskRepository.getAllTasks()

    fun searchTask(query: String) = taskRepository.searchTask(query)
}