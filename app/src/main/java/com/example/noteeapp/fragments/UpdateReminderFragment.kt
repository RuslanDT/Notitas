package com.example.noteeapp.fragments

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.noteeapp.*
import com.example.noteeapp.databinding.FragmentUpdateReminderBinding
import com.example.noteeapp.model.Reminder
import com.example.noteeapp.util.DatePicker
import com.example.noteeapp.util.ReminderBroadCast
import com.example.noteeapp.util.TimerPicker
import com.example.noteeapp.viewModel.ReminderViewModel
import java.util.*

class UpdateReminderFragment : Fragment() {

    private var _binding: FragmentUpdateReminderBinding? = null
    private val binding: FragmentUpdateReminderBinding
        get() = _binding!!

    private lateinit var reminderViewModel: ReminderViewModel
    private val args: updateReminderArgs by navArgs()
    private lateinit var currentReminder: Reminder

    private var day: Int = 0
    private var month: Int = 0
    private var year: Int = 0
    private var hour: Int = 0
    private var minute: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.update_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateReminderBinding.inflate(layoutInflater, container, false)
        reminderViewModel = (activity as MainActivity).reminderViewModel



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentReminder = args.reminder!!
        binding.titleTaskUpdate.setText("eeeee")
        binding.taskBodyUpdate.setText(currentReminder.body)
        binding.textDateUpdate.text = currentReminder.dateReminder
        binding.timerTextUpdate.text = currentReminder.hourRemainder

        binding.CalendarioUpdate.setOnClickListener{
            showDatePickerDialog()
        }
        //para ecuchar el click de abrir timer
        binding.timerUpdate.setOnClickListener{
            showTimePickerDialog()
        }

        binding.fabUpdateReminder.setOnClickListener {
            val title = binding.titleTaskUpdate.text.toString().trim()
            val body = binding.taskBodyUpdate.text.toString().trim()

            if (title.isNotEmpty()) {
//                val intent = Intent(requireContext(), ReminderBroadCast(title,body, currentReminder.idTask).onReceive(requireContext(), intent = null)::class.java)
                val intent = Intent(requireContext(), ReminderBroadCast::class.java)
                intent.putExtra("id", currentReminder.idTask)
                val pendingIntent = PendingIntent.getBroadcast(requireContext(), currentReminder.idTask, intent, 0)

                //se programa la notificacion
                val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.set(AlarmManager.RTC_WAKEUP, 6*1000, pendingIntent)
                Toast.makeText(requireContext(), "Alarma programada", Toast.LENGTH_SHORT).show()
            } else {
                activity?.toast("Agrega un titulo")
            }
        }
    }

    //para el dia, mes, anio
    private fun showDatePickerDialog(){
        val newFragment = DatePicker{ day, month, year -> onDateSelected(day, month, year)}
        activity?.let { newFragment.show(it.supportFragmentManager, "datePicker")}
    }

    @SuppressLint("SetTextI18n")
    private fun onDateSelected(day: Int, month: Int, year: Int ){
        binding.textDateUpdate.text = ("$day/$month/$year")
        this.day = day
        this.month = month
        this.year = year
    }

    //para la hora
    private fun showTimePickerDialog(){
        val newFragment = TimerPicker{ hour, minute -> onTimeSelected(hour, minute)}
        activity?.let { newFragment.show(it.supportFragmentManager, "TimePicker")}
    }

    @SuppressLint("SetTextI18n")
    private fun onTimeSelected(hour: Int, minute: Int ){
        binding.timerTextUpdate.text = ("$hour : $minute")
        this.hour = hour
        this.minute = minute
    }

    private fun deleteNote() {
        AlertDialog.Builder(activity).apply {
            setTitle("Borrar Nota")
            setMessage("¿Estas seguro de querer borrar la nota?")

            setPositiveButton("BORRAR") { _, _ ->
                reminderViewModel.deleteReminder(currentReminder)
                view?.findNavController()
                    ?.navigate(updateReminderDirections.actionUpdateReminderToHomeReminderFragment())
            }

            setNegativeButton("CANCELAR", null)
        }.create().show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_menu -> {
                deleteNote()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}