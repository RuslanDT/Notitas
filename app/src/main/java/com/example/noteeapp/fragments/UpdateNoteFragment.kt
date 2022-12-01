package com.example.noteeapp.fragments

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val LOG_TAG = "AudioRecordTest"
class UpdateNoteFragment : Fragment() {

    private var _binding: FragmentUpdateNoteBinding? = null
    private val binding: FragmentUpdateNoteBinding
        get() = _binding!!

    private lateinit var noteViewModel: NoteViewModel
    private val args: UpdateNoteFragmentArgs by navArgs()
    private lateinit var currentNote: Note

    val REQUEST_IMAGE_CAPTURE  = 10
    val REQUEST_VIDEO_CAPTURE = 20

    lateinit var currentVideoPath: String
    var photoURI: Uri? = null
    var videoURI: Uri? = null

    private var fileName: String = ""
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var mStartRecording: Boolean = true
    var mStartPlaying = true
    lateinit var requestPermissionLauncher: ActivityResultLauncher<String>


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

        var dir = "android.resource://" + requireActivity().packageName + "/" + R.raw.espectro_audio
        binding.videoEspectroUpdate.setVideoURI(dir.toUri())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentNote = args.note!!
        binding.etNoteTitleUpdate.setText(currentNote.noteTitle)
        binding.etNoteBodyUpdate.setText(currentNote.noteBody)

        //validacion para pintar imagen, video y audio
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

        if(currentNote.noteAudio != ""){
            fileName = currentNote.noteAudio
            binding.layoutAudioUpdate.visibility = View.VISIBLE
            binding.videoEspectroUpdate.visibility = View.VISIBLE
            binding.videoEspectroUpdate.start()
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
                val updetedNote = Note(currentNote.id, title, body, imagen, video, fileName, currentDate.toString())
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

        binding.btnFotoUpdate.setOnClickListener {
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

        binding.btnVideoUpdate.setOnClickListener {
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

        binding.btnVozUpdtae.setOnClickListener{
            binding.layoutAudioUpdate.visibility = View.VISIBLE

        }

        binding.btnGrabarUpdate.setOnClickListener {
            binding.videoEspectroUpdate.visibility = View.VISIBLE
            binding.videoEspectroUpdate.start()
            grabar()
            onRecord(mStartRecording)
            when (mStartRecording) {
                true -> binding.btnGrabarUpdate.setImageResource(R.drawable.stop64)
                false -> binding.btnGrabarUpdate.setImageResource(R.drawable.grabar_icon)
            }

            mStartRecording = !mStartRecording
        }

        binding.btnReproducirUpdate.setOnClickListener {
            onPlay(mStartPlaying)
            mStartPlaying = !mStartPlaying
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

    private fun onPlay(start: Boolean) = if (start) {
        startPlaying()
    } else {
        stopPlaying()
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
        }
    }

    private fun onRecord(start: Boolean) = if (start) {
        iniciarGraabacion()
    } else {
        stopRecording()
    }

    private fun iniPerm(){
        // Register the permissions callback, which handles the user's response to the
// system permissions dialog. Save the return value, an instance of
// ActivityResultLauncher. You can use either a val, as shown in this snippet,
// or a lateinit var in your onAttach() or onCreate() method.
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }

    }

    private fun grabar() {
        revisarPermisos()
    }

    private fun revisarPermisos() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                "android.permission.RECORD_AUDIO"
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.

            }
            shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO") -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
                //showInContextUI(...)
                //Toast.makeText(applicationContext, "Debes dar perimso para grabar audios", Toast.LENGTH_SHORT).show()
                MaterialAlertDialogBuilder(requireContext()
                )
                    .setTitle("Title")
                    .setMessage("Debes dar perimso para grabar audios")
                    .setNegativeButton("Cancel") { dialog, which ->
                        // Respond to negative button press
                    }
                    .setPositiveButton("OK") { dialog, which ->
                        // Respond to positive button press
                        /*requestPermissionLauncher.launch(
                            "android.permission.RECORD_AUDIO")*/

                        // You can directly ask for the permission.
                        requestPermissions(
                            arrayOf("android.permission.RECORD_AUDIO",
                                "android.permission.WRITE_EXTERNAL_STORAGE"),
                            1001)
                    }
                    .show()
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                /*requestPermissionLauncher.launch(
                    "android.permission.RECORD_AUDIO")*/
                requestPermissions(
                    arrayOf("android.permission.RECORD_AUDIO",
                        "android.permission.WRITE_EXTERNAL_STORAGE"),
                    1001)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1001 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                            grantResults[1] == PackageManager.PERMISSION_GRANTED
                            )) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    iniciarGraabacion()
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun iniciarGraabacion() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            createAudioFile()
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }

            start()
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }


    @Throws(IOException::class)
    fun createAudioFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
        //val storageDir: File? = filesDir
        return File.createTempFile(
            "AUDIO_${timeStamp}_", /* prefix */
            ".mp3", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            fileName = absolutePath

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            binding.enotaImgenUpdate.setImageURI(photoURI)
            binding.enotaImgenUpdate.visibility = View.VISIBLE
        }

        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            binding.eNotaVideoUpdate.setVideoURI(videoURI)
            binding.eNotaVideoUpdate.visibility = View.VISIBLE
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