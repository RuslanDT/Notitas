package com.example.noteeapp.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.noteeapp.MainActivity
import com.example.noteeapp.R
import com.example.noteeapp.adapter.NoteAdapter
import com.example.noteeapp.adapter.ReminderAdapter
import com.example.noteeapp.databinding.FragmentReminderHomeBinding
import com.example.noteeapp.viewModel.ReminderViewModel


class HomeReminderFragment : Fragment() {
    private var _binding: FragmentReminderHomeBinding? = null
    private val binding
        get() = _binding!!
    private lateinit var reminderViewModel: ReminderViewModel
    private lateinit var reminderAdapter: ReminderAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReminderHomeBinding.inflate(inflater, container, false)

        reminderViewModel = (activity as MainActivity).reminderViewModel

        _binding!!.botonNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.action_tareas -> {
                    view?.findNavController()?.navigate(HomeReminderFragmentDirections.actionHomeReminderFragmentToHomeTaskFragment()); true
                }
                R.id.action_notas -> {
                    view?.findNavController()?.navigate(HomeReminderFragmentDirections.actionHomeReminderFragmentToHomeFragment()); true
                }
                else -> {false}
            }
        }
        setUpRecyclerView()
        return binding.root
    }

    private fun setUpRecyclerView() {
        reminderAdapter = ReminderAdapter()
        binding.notesRV.apply {
            layoutManager = StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )
            setHasFixedSize(true)
            adapter = reminderAdapter
            reminderViewModel.getAllReminders().observe(viewLifecycleOwner) { reminder ->
                reminderAdapter.differ.submitList(reminder)
                reminder.updateUI()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment_container, fragment)
        fragmentTransaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.home_main, menu)

        val searchItem = menu.findItem(R.id.menu_search).actionView as SearchView
        searchItem.isSubmitButtonEnabled = true

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun <E> List<E>.updateUI() {
        if (isNotEmpty()) {
            binding.notesRV.visibility = View.VISIBLE
            binding.tvNoNoteAvailable.visibility = View.GONE
        } else {
            binding.notesRV.visibility = View.GONE
            binding.tvNoNoteAvailable.visibility = View.VISIBLE
        }
    }




}



