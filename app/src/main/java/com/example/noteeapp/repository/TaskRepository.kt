package com.example.noteeapp.repository

import com.example.noteeapp.database.NoteDataBase
import com.example.noteeapp.database.TaskDataBase
import com.example.noteeapp.model.Note
import com.example.noteeapp.model.Task

class TaskRepository(private val db: TaskDataBase) {

    suspend fun addTask(task: Task) = db.getTaskDao().addTask(task)
    suspend fun updateTask(task: Task) = db.getTaskDao().updateTask(task)
    suspend fun deleteTask(task: Task) = db.getTaskDao().deleteTask(task)
    fun getAllTasks() = db.getTaskDao().getAllTasks()
    fun searchTask(query: String) = db.getTaskDao().searchTaks(query)

}