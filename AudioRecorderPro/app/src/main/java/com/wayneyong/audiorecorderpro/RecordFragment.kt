package com.wayneyong.audiorecorderpro

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.wayneyong.audiorecorderpro.databinding.FragmentRecordBinding
import kotlinx.android.synthetic.main.fragment_record.*
import java.io.File

private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
/**
 * Why use view model? (Model View ViewModel)
 * Separation of concern, view model sit behind the fragment, allow to observe things in view model, if things change in view model,
 * every time rotate the device, fragment get destroyed and remade. If there is a lot of code to allow to database and get data from online,
 * we don't really need to reinitialise the code when rotating the device or switch in in or out of app, instead we rather to have view model
 * that store data somewhere that won't be destroyed easily as the rotation of the device.
 *
 * We want to keep some stuff alive that dictate the business logic or should or shouldn't do, let the display logic takes care of itself
 * It holds the states of current app, fragment is responsible to display that state
 * viewmodel holds state, are recording or show share or not show share button, fragment get a hook on that viewmodel, listen to it and watch things
 * happen inside it
 *
 * Publisher-Subscriber Model = ViewModel is the publisher, Interface is the subscriber
 * if viewModel changes something, interface get notified about the changes and react accordingly
 * same kind as of Observable, but LiveData knows about lifeCycle, when lifeCycle get destroyed, we don't want to left variables floating in air
 * that don't belong to anyone, therefore never know if they should be deleted or not, liveData take care of that if viewModel goes away, we can
 * get rid of these live data variable, get rid of memory leaks in the app
 * */

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class RecordFragment : Fragment() {

    private lateinit var binding: FragmentRecordBinding
    private lateinit var viewModel: RecordViewModel
    private val LOG_TAG = "RecordFragment"


    // Requesting permission to RECORD_AUDIO
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        ) //do the base function
        //do the custom code
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) {
            //inform user to accept permission
            informUserPermissionsDenied();
        }
    }

    //hide the class to not allow access from other act/fragment
    private fun informUserPermissionsDenied() {
        //TODO
    }


    //method or function happen automatically when a fragments are created, we cannot control them but we can intercept them, overriding it
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_record,
            container,
            false
        )
        viewModel = ViewModelProvider(this).get(RecordViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }


    //everything has been made, as it layout the fragment record, identify widget and define here
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let {
            val externalStorageVolumes: Array<out File> =
                ContextCompat.getExternalFilesDirs(it, null)
            viewModel.setStorageDir(externalStorageVolumes[0].absolutePath + "/")
//            viewModel.setFilename(Utils().getDateTimeReadable())
            /**
             * We go thru the context to get the file location, context refer to activity or fragment
             *  Do not pass the context to the viewModel: Mistake! because the context could disappear, when the fragment is destroyed or created
             *  then viewModel will have the reference to context that doesnt exist anymore.
             *
             *  Never pass anything fragment to do with context to the viewModel, viewModel should never know about the display as it could disappear and appear
             */
        }

        activity?.let {

            ActivityCompat.requestPermissions(it, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
        }


        settingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_recordFragment_to_settingsFragment)
        }

        showAllButton.setOnClickListener { findNavController().navigate(R.id.action_recordFragment_to_recordingListFragment) }


        recordButton.setOnClickListener {
            viewModel.onRecord()
        }

        recordFragmentStopImageButton.setOnClickListener {
            viewModel.stopRecording()
        }

        saveButton.setOnClickListener {
            viewModel.setFilename(filenameEditText.text.toString())
            viewModel.changeStoredFilename(filenameEditText.text.toString())
        }
    }


/*
    //todo remove to playfragment
    private var player: MediaPlayer? = null
    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
                Log.e(LOG_TAG, e.message.toString())
            }
        }
    }
    private fun stopPlaying() {
        player?.release()
        player = null
    }

    override fun onStop() {
        super.onStop()
        recorder?.release()
        recorder = null
        player?.release()
        player = null
    }

    **/

    private fun onRecord(start: Boolean) {
        if (start) {
            viewModel.startRecording()
        } else {
            viewModel.pauseRecording()
        }
    }
}