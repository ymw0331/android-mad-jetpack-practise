package com.wayneyong.audiorecorderpro

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import java.io.File
import java.io.IOException

private const val REQUEST_RECORD_AUDIO_PERMISSION = 200


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class RecordFragment : Fragment() {

    private val LOG_TAG = "RecordFragment"

    private var fileName: String = ""

    var mStartRecording = true
    var mStartPlaying = true

    //    private var recordButton: RecordButton? = null
    private var recorder: MediaRecorder? = null

    //    private var playButton: PlayButton? = null
    private var player: MediaPlayer? = null

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
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    //everything has been made, as it layout the fragment record, identify widget and define here
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let {
            val externalStorageVolumes: Array<out File> =
                ContextCompat.getExternalFilesDirs(it, null)
            fileName = externalStorageVolumes[0].absolutePath + "/audiorecordtest.3gp"

        }



        activity?.let {

            ActivityCompat.requestPermissions(it, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
        }
//        ActivityCompat.requestPermissions(requireActivity(), permissions, REQUEST_RECORD_AUDIO_PERMISSION)


        view.findViewById<ImageButton>(R.id.settingsButton).setOnClickListener {
            findNavController().navigate(R.id.action_recordFragment_to_settingsFragment)
        }

        view.findViewById<Button>(R.id.showAllButton)
            .setOnClickListener { findNavController().navigate(R.id.action_recordFragment_to_recordingListFragment) }

        view.findViewById<ImageButton>(R.id.recordFragmentPlayImageButton).setOnClickListener {
            onPlay(mStartPlaying)
            mStartPlaying = !mStartPlaying
        }

        view.findViewById<Button>(R.id.recordButton).setOnClickListener {
            onRecord(mStartRecording)
            mStartRecording = !mStartRecording

        }

    }


    private fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        stopRecording()
    }

    private fun onPlay(start: Boolean) = if (start) {
        startPlaying()
    } else {
        stopPlaying()
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

    private fun stopPlaying() {
        player?.release()
        player = null
    }

    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
                Log.e(LOG_TAG, e.message.toString())
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

    override fun onStop() {
        super.onStop()
        recorder?.release()
        recorder = null
        player?.release()
        player = null
    }

}