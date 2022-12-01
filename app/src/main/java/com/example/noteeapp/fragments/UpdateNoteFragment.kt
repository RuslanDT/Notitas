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
import com.example.noteeapp.model.Note
import com.example.noteeapp.toast
import com.example.noteeapp.viewModel.NoteViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class UpdateNoteFragment : Fragment() {

    private var _binding: FragmentUpdateNoteBinding? = null
    private val binding: FragmentUpdateNoteBinding
        get() = _binding!!

    private lateinit var noteViewModel: NoteViewModel
    private val args: UpdateNoteFragmentArgs by navArgs()
    private lateinit var currentNote: Note

    val REQUEST_IMAGE_CAPTURE  = 10
    lateinit var currentVideoPath: String
    var photoURI: Uri? = null
    var videoURI: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateNoteBinding.inflate(layoutInflater, container, false)
        noteViewModel = (activity as MainActivity).noteViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentNote = args.note!!
        binding.etNoteTitleUpdate.setText(currentNote.noteTitle)
        binding.etNoteBodyUpdate.setText(currentNote.noteBody)

        //validacion para pintar imagen y video
        if(currentNote.noteImagen != ""){
            photoURI = currentNote.noteImagen.toUri()
            binding.enotaImgenUpdate.setImageURI(photoURI)
            binding.enotaImgenUpdate.visibility = View.VISIBLE
        }

        if(currentNote.noteVideo != ""){
            videoURI = currentNote.noteVideo.toUri()
            binding.eNotaVideoUpdate.setVideoURI(videoURI)
            binding.eNotaVideoUpdate.visibility = View.VISIBLE
        }


        binding.fabUpdate.setOnClickListener {
            val title = binding.etNoteTitleUpdate.text.toString().trim()
            val body = binding.etNoteBodyUpdate.text.toString().trim()
            val timeStamp = SimpleDateFormat("dd/MM/yyyy")
            val currentDate = timeStamp.format(Date())

            var imagen = ""
            var video = ""

            if (photoURI != null){
                imagen = photoURI.toString()
            }

            if(videoURI != null){
                video = videoURI.toString()
            }

            if (title.isNotEmpty()) {
                val updetedNote = Note(currentNote.id, title, body, imagen, video, "", currentDate.toString())
                noteViewModel.updateNote(updetedNote)
                activity?.toast("Nota actualizada")
                it.findNavController()
                    .navigate(UpdateNoteFragmentDirections.actionUpdateNoteFragmentToHomeFragment())
            } else {
                activity?.toast("Agrega un titulo")
            }
        }

        binding.enotaImgenUpdate.setOnLongClickListener {
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle(R.string.alerta_borrarIMG_titulo)
                .setMessage(R.string.alerta_borrarIMG_mensaje)
                .setNegativeButton(R.string.cancelar) { view, _ ->
                    view.dismiss()
                }
                .setPositiveButton(R.string.aceptar) { view, _ ->
                    photoURI = null
                    binding.enotaImgenUpdate.visibility = View.GONE
                    Toast.makeText(requireContext(), "Se ha eliminado", Toast.LENGTH_SHORT).show()
                    view.dismiss()
                }
                .setCancelable(false)
                .create()

            dialog.show()
            return@setOnLongClickListener false
        }

        binding.eNotaVideoUpdate.setOnLongClickListener {
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle(R.string.alerta_borrarVIDEO_titulo)
                .setMessage(R.string.alerta_borrarVIDEO_mensaje)
                .setNegativeButton(R.string.cancelar) { view, _ ->
                    view.dismiss()
                }
                .setPositiveButton(R.string.aceptar) { view, _ ->
                    videoURI = null
                    binding.eNotaVideoUpdate.visibility = View.GONE
                    Toast.makeText(requireContext(), "Se ha eliminado", Toast.LENGTH_SHORT).show()
                    view.dismiss()
                }
                .setCancelable(false)
                .create()

            dialog.show()
            return@setOnLongClickListener false
        }

        binding.eNotaVideoUpdate.setOnClickListener{
            binding.eNotaVideoUpdate.start()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.update_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    private fun deleteNote() {
        AlertDialog.Builder(activity).apply {
            setTitle("Borrar Nota")
            setMessage("¿Estas seguro de querer borrar la nota?")

            setPositiveButton("BORRAR") { _, _ ->
                noteViewModel.deleteNote(currentNote)
                view?.findNavController()
                    ?.navigate(UpdateNoteFragmentDirections.actionUpdateNoteFragmentToHomeFragment())
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            binding.enotaImgenUpdate.setImageURI(
                photoURI
            )
        }
    }

    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        //val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val storageDir: File? = activity?.filesDir
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
            currentVideoPath = absolutePath
        }
    }
}