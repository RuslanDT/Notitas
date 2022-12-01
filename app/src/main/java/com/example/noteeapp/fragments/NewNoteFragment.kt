package com.example.noteeapp.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.example.noteeapp.MainActivity
import com.example.noteeapp.R
import com.example.noteeapp.databinding.FragmentNewNoteBinding
import com.example.noteeapp.model.Note
import com.example.noteeapp.toast
import com.example.noteeapp.viewModel.NoteViewModel
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class NewNoteFragment : Fragment() {

    private var _binding: FragmentNewNoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var mView: View

    val REQUEST_IMAGE_CAPTURE  = 10
    val REQUEST_VIDEO_CAPTURE = 20

    lateinit var currentVideoPath: String
    lateinit var currentPhotoPath: String
    var photoURI: Uri? = null
    var videoURI: Uri? = null

    private var fileName: String = ""
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        _binding = FragmentNewNoteBinding.inflate(inflater, container, false)

        var dir = "android.resource://" + requireActivity().packageName + "/" + R.raw.espectro_audio
        binding.videoEspectro.setVideoURI(dir.toUri())
        binding.videoEspectro.start()

        binding.btnFoto.setOnClickListener {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(requireActivity().packageManager).also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File

                        null
                    }

                    // Continue only if the File was successfully created
                    photoFile?.also {
                        photoURI = FileProvider.getUriForFile(
                            requireContext(),
                            "com.example.noteeapp.fileprovider",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    }
                }
            }
        }

        binding.btnVideo.setOnClickListener {
            Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
                takeVideoIntent.resolveActivity(requireActivity().packageManager).also {

                    // Create the File where the photo should go
                    val videoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File

                        null
                    }

                    // Continue only if the File was successfully created
                    videoFile?.also {
                        videoURI = FileProvider.getUriForFile(
                            requireContext(),
                            "com.example.noteeapp.fileprovider",
                            it
                        )
                        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI)
                        startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
                    }
                }
            }
        }

        binding.enoteImagen.setOnLongClickListener {
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle(R.string.alerta_borrarIMG_titulo)
                .setMessage(R.string.alerta_borrarIMG_mensaje)
                .setNegativeButton(R.string.cancelar) { view, _ ->
                    view.dismiss()
                }
                .setPositiveButton(R.string.aceptar) { view, _ ->
                    photoURI = null
                    binding.enoteImagen.visibility = View.GONE
                    Toast.makeText(requireContext(), "Se ha eliminado", Toast.LENGTH_SHORT).show()
                    view.dismiss()
                }
                .setCancelable(false)
                .create()

            dialog.show()
            return@setOnLongClickListener false
        }

        binding.eNoteVideo.setOnLongClickListener {
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle(R.string.alerta_borrarVIDEO_titulo)
                .setMessage(R.string.alerta_borrarVIDEO_mensaje)
                .setNegativeButton(R.string.cancelar) { view, _ ->
                    view.dismiss()
                }
                .setPositiveButton(R.string.aceptar) { view, _ ->
                    videoURI = null
                    binding.eNoteVideo.visibility = View.GONE
                    Toast.makeText(requireContext(), "Se ha eliminado", Toast.LENGTH_SHORT).show()
                    view.dismiss()
                }
                .setCancelable(false)
                .create()

            dialog.show()
            return@setOnLongClickListener false
        }

        binding.eNoteVideo.setOnClickListener{
            binding.eNoteVideo.start()
        }

        return binding.root
    }

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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteViewModel = (activity as MainActivity).noteViewModel
        mView = view
    }

    private fun saveNote(view: View) {
        val noteTitle = binding.etNoteTitle.text.toString().trim()
        val noteBody = binding.etNoteBody.text.toString().trim()
        val timeStamp = SimpleDateFormat("dd/MM/yyyy")
        val currentDate = timeStamp.format(Date())
        var imagen = ""
        var video = ""

        if(photoURI != null){
            imagen = photoURI.toString()
        }

        if(videoURI != null){
            video = videoURI.toString()
        }

        if (noteTitle.isNotEmpty()) {
            val note = Note(0, noteTitle, noteBody, imagen, video, "", currentDate.toString())
            noteViewModel.addNote(note)
            Snackbar.make(
                view,
                "Nota guardada",
                Snackbar.LENGTH_SHORT,
            ).show()
            replaceFragment(HomeFragment())
        } else {
            activity?.toast("Agrega un titulo")
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
        inflater.inflate(R.menu.new_note_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_menu -> {
                saveNote(mView)
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
            binding.enoteImagen.setImageURI(photoURI)
            binding.enoteImagen.visibility = View.VISIBLE
        }

        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            binding.eNoteVideo.setVideoURI(videoURI)
            binding.eNoteVideo.visibility = View.VISIBLE
        }
    }
}