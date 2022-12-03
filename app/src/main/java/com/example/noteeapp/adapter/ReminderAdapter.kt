package com.example.noteeapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.noteeapp.databinding.ReminderItemBinding
import com.example.noteeapp.fragments.HomeFragment
import com.example.noteeapp.fragments.HomeFragmentDirections
import com.example.noteeapp.fragments.HomeTaskFragmentDirections
import com.example.noteeapp.model.Note
import com.example.noteeapp.model.Reminder

class ReminderAdapter : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        return ReminderViewHolder(
            ReminderItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val currentReminder = differ.currentList[position]

        holder.itemBinding.tvReminderTitle.text = currentReminder.title
        holder.itemBinding.tvReminderBody.text = currentReminder.body



        holder.itemBinding.tvDate.text = currentReminder.dateReminder

        val random = java.util.Random()
        val color = Color.argb(
            255,
            random.nextInt(256),
            random.nextInt(256),
            random.nextInt(256)
        )
        holder.itemBinding.viewColor.setBackgroundColor(color)
        //falta pasarle el parametro si se quiere hacer otra ventana para actualizar
        holder.itemView.setOnClickListener {
            view ->
            val direction = HomeTaskFragmentDirections.actionHomeTaskFragmentToHomeReminderFragment()
            view.findNavController().navigate(direction)
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    class ReminderViewHolder(val itemBinding: ReminderItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

    }

    private val diffUtilCall = object : DiffUtil.ItemCallback<Reminder>() {
        override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, diffUtilCall)

}