package com.example.noteeapp

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.noteeapp.database.NoteDataBase
import com.example.noteeapp.database.ReminderDataBase
import com.example.noteeapp.database.TaskDataBase
import com.example.noteeapp.databinding.ActivityMainBinding
import com.example.noteeapp.fragments.HomeFragment
import com.example.noteeapp.fragments.HomeTaskFragment
import com.example.noteeapp.repository.NoteRepository
import com.example.noteeapp.repository.ReminderRepository
import com.example.noteeapp.repository.TaskRepository
import com.example.noteeapp.viewModel.*

class MainActivity : AppCompatActivity() {
    lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    lateinit var noteViewModel: NoteViewModel
    lateinit var taskViewModel: TaskViewModel
    lateinit var reminderViewModel: ReminderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        navController = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)!!
            .findNavController()

        /*binding.botonNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.action_notas -> replaceFragment(HomeFragment())
                R.id.action_tareas -> replaceFragment(HomeTaskFragment())
                else -> { }
            }
            true
        }*/


        setUp()
        setUpTask()
        setUpReminder()
    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment_container, fragment)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.updateNoteFragment) {
            this.toast("No Updates")
        }
        super.onBackPressed()
    }

    private fun setUp() {
        val noteDataBase = NoteDataBase.getInstance(this)
        val noteRepository = NoteRepository(
            noteDataBase
        )

        val viewModelProviderFactory = NoteViewModelProviderFactory(
            application,
            noteRepository
        )

        noteViewModel = ViewModelProvider(
            this,
            viewModelProviderFactory
        )
            .get(NoteViewModel::class.java)
    }
    private fun setUpTask() {
        val taskDataBase = TaskDataBase.getInstance(this)
        val taskRepository = TaskRepository(
            taskDataBase
        )

        val viewModelProviderFactory = TaskViewModelProviderFactory(
            application,
            taskRepository
        )

        taskViewModel = ViewModelProvider(
            this,
            viewModelProviderFactory
        )
            .get(TaskViewModel::class.java)
    }
    private fun setUpReminder() {
        val reminderDataBase = ReminderDataBase.getInstance(this)
        val reminderRepository = ReminderRepository(
            reminderDataBase
        )

        val viewModelProviderFactory = ReminderViewModelProviderFactory(
            application,
            reminderRepository
        )

        reminderViewModel = ViewModelProvider(
            this,
            viewModelProviderFactory
        )
            .get(ReminderViewModel::class.java)
    }
}