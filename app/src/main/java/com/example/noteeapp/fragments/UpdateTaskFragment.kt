package com.example.noteeapp.fragments

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.noteeapp.MainActivity
import com.example.noteeapp.R
import com.example.noteeapp.databinding.FragmentUpdateNoteBinding
import com.example.noteeapp.databinding.FragmentUpdateTaskBinding
import com.example.noteeapp.model.Note
import com.example.noteeapp.model.Task
import com.example.noteeapp.toast
import com.example.noteeapp.viewModel.NoteViewModel
import com.example.noteeapp.viewModel.TaskViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class UpdateTaskFragment : Fragment() {

    private var _binding: FragmentUpdateTaskBinding? = null
    private val binding: FragmentUpdateTaskBinding
        get() = _binding!!

    private lateinit var taskViewModel: TaskViewModel
    private val args: UpdateTaskFragmentArgs by navArgs()
    private lateinit var currentTask: Task



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateTaskBinding.inflate(layoutInflater, container, false)
        taskViewModel = (activity as MainActivity).taskViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentTask = args.task!!
        binding.etTaskTitleUpdate.setText(currentTask.taskTitle)
        binding.etTaskBodyUpdate.setText(currentTask.taskBody)



        binding.fabUpdate.setOnClickListener {
            val title = binding.etTaskTitleUpdate.text.toString().trim()
            val body = binding.etTaskBodyUpdate.text.toString().trim()
            val timeStamp = SimpleDateFormat("dd/MM/yyyy")
            val currentDate = timeStamp.format(Date())

            if (title.isNotEmpty()) {
                val updetedTask = Task(currentTask.id, title, body, currentDate.toString(),"")
                taskViewModel.updateTask(updetedTask)
                activity?.toast("Tarea actualizada")
                it.findNavController()
                    .navigate(UpdateTaskFragmentDirections.actionUpdateTaskFragmentToHomeTaskFragment())
            } else {
                activity?.toast("Agrega un titulo")
            }
        }



    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.update_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    private fun deleteNote() {
        AlertDialog.Builder(activity).apply {
            setTitle("Borrar Tarea")
            setMessage("¿Estas seguro de querer borrar la Tarea?")

            setPositiveButton("BORRAR") { _, _ ->
                taskViewModel.deleteTask(currentTask)
                view?.findNavController()
                    ?.navigate(UpdateTaskFragmentDirections.actionUpdateTaskFragmentToHomeTaskFragment())
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