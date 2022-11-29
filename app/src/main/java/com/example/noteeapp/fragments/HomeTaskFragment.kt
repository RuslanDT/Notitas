package com.example.noteeapp.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.noteeapp.MainActivity
import com.example.noteeapp.R
import com.example.noteeapp.adapter.NoteAdapter
import com.example.noteeapp.adapter.TaskAdapter
import com.example.noteeapp.databinding.FragmentHomeBinding
import com.example.noteeapp.databinding.FragmentTaskHomeBinding
import com.example.noteeapp.viewModel.NoteViewModel
import com.example.noteeapp.viewModel.TaskViewModel


class HomeTaskFragment : Fragment(), SearchView.OnQueryTextListener {
    private var _binding: FragmentTaskHomeBinding? = null
    private val binding
        get() = _binding!!
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var taskAdapter: TaskAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskHomeBinding.inflate(inflater, container, false)

        taskViewModel = (activity as MainActivity).taskViewModel

        setUpRecyclerView()
        return binding.root
    }

    private fun setUpRecyclerView() {
        taskAdapter = TaskAdapter()
        binding.notesRV.apply {
            layoutManager = StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )
            setHasFixedSize(true)
            adapter = taskAdapter
            taskViewModel.getAllTasks().observe(viewLifecycleOwner) { tasks ->
                taskAdapter.differ.submitList(tasks)
                tasks.updateUI()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabAddNote.setOnClickListener {
            //it.findNavController().navigate(R.id.action_homeFragment_to_newNoteFragment)
            mostrarPopUp(view)
        }
    }
    //boton 2.0
    //crear funcion para mostrar el PopUp
    fun mostrarPopUp(v : View){
        this.context?.let {
            PopupMenu(it, v).apply {
                inflate(R.menu.menu_emergente)
                setOnMenuItemClickListener {
                    when(it!!.itemId){
                        R.id.nota_menu -> {
                            v.findNavController().navigate(R.id.action_homeTaskFragment_to_newNoteFragment)
                            true
                        }
                        R.id.tarea_menu -> {
                            v.findNavController().navigate(R.id.action_homeTaskFragment_to_newTaskFragment)
                            true
                        }
                        else -> false
                    }
                }
            }.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.home_main, menu)

        val searchItem = menu.findItem(R.id.menu_search).actionView as SearchView
        searchItem.isSubmitButtonEnabled = true
        searchItem.setOnQueryTextListener(this)

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

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchNote(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            searchNote(newText)
        }
        return true
    }

    private fun searchNote(query: String?) {
        val searchQuery = "%$query%"
        taskViewModel.searchTask(searchQuery).observe(this) { tasks ->
            taskAdapter.differ.submitList(tasks)
        }

    }
}



