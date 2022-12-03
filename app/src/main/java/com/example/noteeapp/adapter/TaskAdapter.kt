package com.example.noteeapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.noteeapp.databinding.TaskItemBinding
import com.example.noteeapp.fragments.HomeFragmentDirections
import com.example.noteeapp.fragments.HomeTaskFragmentDirections
import com.example.noteeapp.model.Task
import kotlin.random.Random

class TaskAdapter : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            TaskItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = differ.currentList[position]

        holder.itemBinding.TitleTask.text = currentTask.taskTitle
        holder.itemBinding.TaskBody.text = currentTask.taskBody
        holder.itemBinding.date.text = currentTask.initialDate

        val random = java.util.Random()
        val color = Color.argb(
            255,
            random.nextInt(256),
            random.nextInt(256),
            random.nextInt(256)
        )
        holder.itemBinding.viewColor.setBackgroundColor(color)

        holder.itemView.setOnClickListener {
                view ->
            val direction = HomeTaskFragmentDirections.actionHomeTaskFragmentToUpdateTaskFragment(currentTask)
            view.findNavController().navigate(direction)
        }



    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    class TaskViewHolder(val itemBinding: TaskItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

    }

    private val diffUtilCall = object : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, diffUtilCall)

}