package com.example.noteeapp.adapter

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.noteeapp.R
import com.example.noteeapp.databinding.ReminderItemBinding
import com.example.noteeapp.fragments.HomeReminderFragmentDirections
import com.example.noteeapp.fragments.UpdateNoteFragmentDirections
import com.example.noteeapp.model.Reminder
import com.example.noteeapp.viewModel.ReminderViewModel
import androidx.navigation.findNavController

class ReminderAdapter(var reminderVM: ReminderViewModel) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    lateinit var context : Context
    //lateinit var reminderVM: ReminderViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        context = parent.context

        return ReminderViewHolder(
            ReminderItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val currentReminder = differ.currentList[position]
        //reminderVM = (ReminderViewHolder as ReminderViewModel)

        holder.itemBinding.tvReminderTitle.text = currentReminder.title
        holder.itemBinding.tvReminderBody.text = currentReminder.hourRemainder

        holder.itemBinding.tvDate.text = currentReminder.dateReminder

        val random = java.util.Random()
        val color = Color.argb(
            255,
            random.nextInt(256),
            random.nextInt(256),
            random.nextInt(256)
        )

        holder.itemBinding.viewColor.setBackgroundColor(color)

        holder.itemView.setOnLongClickListener{
            val dialog = AlertDialog.Builder(context)
                .setTitle(R.string.alerta_borrarVIDEO_titulo)
                .setMessage(R.string.alerta_borrarVIDEO_mensaje)
                .setNegativeButton(R.string.cancelar) { view, _ ->
                    view.dismiss()
                }
                .setPositiveButton(R.string.aceptar) { view, _ ->
                    reminderVM.deleteReminder(currentReminder)
                    Toast.makeText(context, "Reminder eliminado", Toast.LENGTH_SHORT).show()
                }
                .setCancelable(false)
                .create()

            dialog.show()
            return@setOnLongClickListener false
        }

        holder.itemView.setOnClickListener { view ->
            val direction = HomeReminderFragmentDirections.actionHomeReminderFragmentToUpdateReminder(currentReminder)
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