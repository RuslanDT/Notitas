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
import com.example.noteeapp.adapter.TaskAdapter
import com.example.noteeapp.databinding.FragmentHomeBinding
import com.example.noteeapp.viewModel.NoteViewModel


class HomeFragment : Fragment(), SearchView.OnQueryTextListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = _binding!!
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var noteAdapter: NoteAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        noteViewModel = (activity as MainActivity).noteViewModel

        _binding!!.botonNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.action_tareas -> {
                    view?.findNavController()?.navigate(HomeFragmentDirections.actionHomeFragmentToHomeTaskFragment()); true
                }
                R.id.action_notas -> {true}
                else -> {false}
            }
        }
        setUpRecyclerView()
        return binding.root
    }

    private fun setUpRecyclerView() {
        noteAdapter = NoteAdapter()
        binding.notesRV.apply {
            layoutManager = StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )
            setHasFixedSize(true)
            adapter = noteAdapter
            noteViewModel.getAllNotes().observe(viewLifecycleOwner) { notes ->
                noteAdapter.differ.submitList(notes)
                notes.updateUI()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabAddNote.setOnClickListener {
            //replaceFragment(NewNoteFragment())
            it.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToNewNoteFragment())
        }
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
        noteViewModel.searchNote(searchQuery).observe(this) { notes ->
            noteAdapter.differ.submitList(notes)
        }

    }
}



