package com.example.noteeapp.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.noteeapp.MainActivity
import com.example.noteeapp.R
import com.example.noteeapp.databinding.FragmentNewTaskBinding
import com.example.noteeapp.model.Task
import com.example.noteeapp.toast
import com.example.noteeapp.viewModel.TaskViewModel
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

class NewTaskFragment : Fragment() {

    private var _binding: FragmentNewTaskBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var mView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskViewModel = (activity as MainActivity).taskViewModel
        mView = view
    }

    private fun saveTask(view: View) {
        val taskTitle = binding.titleTask.text.toString().trim()
        val taskBody = binding.taskBody.text.toString().trim()
        val taskFinishDate = binding.finishDate.date.toString().trim()

        val taskStartDate = ""
        if (taskTitle.isNotEmpty()) {
            val task = Task(0, taskTitle, taskBody, taskStartDate, taskFinishDate)
            taskViewModel.addTask(task)
            Snackbar.make(
                view,
                "Tarea guardada",
                Snackbar.LENGTH_SHORT,
            ).show()
            findNavController().navigate(R.id.action_newTaskFragment_to_homeTaskFragment)
        } else {
            activity?.toast("Agrega un titulo")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.new_note_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_menu -> {
                saveTask(mView)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}