package com.example.noteeapp.fragments

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.noteeapp.MainActivity
import com.example.noteeapp.R
import com.example.noteeapp.databinding.FragmentNewTaskBinding
import com.example.noteeapp.model.Reminder
import com.example.noteeapp.model.Task
import com.example.noteeapp.toast
import com.example.noteeapp.util.DatePicker
import com.example.noteeapp.util.ReminderBroadCast
import com.example.noteeapp.util.TimerPicker
import com.example.noteeapp.viewModel.ReminderViewModel
import com.example.noteeapp.viewModel.TaskViewModel
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class NewTaskFragment : Fragment() {

    private var _binding: FragmentNewTaskBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var reminderViewModel: ReminderViewModel
    private lateinit var mView: View
    private var day: Int = 0
    private var month: Int = 0
    private var year: Int = 0
    private var hour: Int = 0
    private var minute: Int = 0
    private var milliseconds: ArrayList<Long> = ArrayList()
    private var dateReminder: ArrayList<String> = ArrayList()
    private var hourReminder: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        createNotificationChanel()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewTaskBinding.inflate(inflater, container, false)
        //para escuchar el click de abrir calendario
        binding.Calendario.setOnClickListener{
            showDatePickerDialog()
        }
        //para ecuchar el click de abrir timer
        binding.timer.setOnClickListener{
            showTimePickerDialog()
        }

        binding.btnSaveAlarm.setOnClickListener{
            //para guardar la alarma en un arreglo
            val calendar: Calendar = Calendar.getInstance()
            val CurrentCalendar: Long = System.currentTimeMillis()

            //hacemos un calendario con la fecha que selecciono el usuario para guardar los milisegundos en un array
            calendar.set(this.year, this.month-1, this.day, this.hour, this.minute, 0)

            //tambien pasamos la fecha en texto
            //arraylist de todas las alarmas que guarde
            dateReminder?.add("$year/$month/$day")
            hourReminder?.add("$hour:$minute")
            milliseconds?.add(calendar.timeInMillis - CurrentCalendar)

            Toast.makeText(requireContext(), "Alarma agregada", Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskViewModel = (activity as MainActivity).taskViewModel
        reminderViewModel = (activity as MainActivity).reminderViewModel
        mView = view

    }

    private fun saveTask(view: View) {

        val taskTitle = binding.titleTask.text.toString().trim()
        val taskBody = binding.taskBody.text.toString().trim()

        val timeStamp = SimpleDateFormat("dd/MM/yyyy")
        val currentDate = timeStamp.format(Date())
        var dateFinishInsert = ""
        //para rescatar la fecha del calendar


        if (taskTitle.isNotEmpty()) {
            val task = Task(0, taskTitle, taskBody, currentDate.toString(), dateFinishInsert)
            taskViewModel.addTask(task)
            //crear las alarmas ahora que sabemos que si se guardara la tarea
            var index = 0
            for(i in milliseconds){
                //se crea la notificacion
                val intent = Intent(requireContext(), ReminderBroadCast(taskTitle,taskBody,task.id).onReceive(requireContext(), intent = null)::class.java)
                val pendingIntent = PendingIntent.getBroadcast(requireContext(), task.id, intent, 0)

                //se programa la notificacion
                val alarmManager = activity?.getSystemService(ALARM_SERVICE) as AlarmManager
                alarmManager.set(AlarmManager.RTC_WAKEUP, i, pendingIntent)
                Toast.makeText(requireContext(), "Alarma programada", Toast.LENGTH_SHORT).show()

                //agregamos el reecordatorio a la base de datos
                val reminder = Reminder(0,task.id, dateReminder[index], hourReminder[index], taskTitle, taskBody)
                reminderViewModel.addReminder(reminder)
                index++
            }

            Snackbar.make(
                view,
                "Tarea guardada",
                Snackbar.LENGTH_SHORT,
            ).show()

            val direction = NewTaskFragmentDirections.actionNewTaskFragmentToHomeTaskFragment()
            view.findNavController().navigate(direction)
        } else {
            activity?.toast("Agrega un titulo")
        }
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.new_note_menu, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_menu -> {
                saveTask(mView)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    //para guardar la alarma
    /*
    private fun setAlarm(timeInMillis: Long, title: String, body: String){
        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), MyAlarm::class.java).let { intent ->
            intent.putExtra("Hola", 0)
        }
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, 0)
        alarmManager.set(AlarmManager.RTC, timeInMillis, pendingIntent)
        Toast.makeText(requireContext(), "Alarm programada", Toast.LENGTH_SHORT).show()

    }



    private class MyAlarm : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("Alarm Bell", "Alarm just fired")
        }
    }

     */
    //creamos el canal de notificacion
    private fun createNotificationChanel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "CanalNotificaciones"
            val description = "Canal para tareas"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("notifications", name, importance)
            channel.description = description

            val notificationManager = getSystemService(requireContext(),NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)

        }
    }

    //para el dia, mes, anio
    private fun showDatePickerDialog(){
        val newFragment = DatePicker{ day, month, year -> onDateSelected(day, month, year)}
        activity?.let { newFragment.show(it.supportFragmentManager, "datePicker")}
    }

    @SuppressLint("SetTextI18n")
    private fun onDateSelected(day: Int, month: Int, year: Int ){
        binding.textDate.text = ("$day/$month/$year")
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
        binding.timerText.text = ("$hour : $minute")
        this.hour = hour
        this.minute = minute
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}