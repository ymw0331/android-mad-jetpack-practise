package com.wayneyong.audiorecorderpro

import android.media.MediaRecorder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import java.io.IOException

class RecordViewModel : ViewModel() {

    //val cannot change the variable in regular way
    //MutableLiveData is a container that can be changed whats inside it

    private val LOG_TAG = "RecordViewModel"
    private val _recordButtonText = MutableLiveData<String>()
    val recordButtontext: LiveData<String>
        get() = _recordButtonText

    private val _showStopBtn = MutableLiveData<Boolean>()
    val showStopBtn: LiveData<Boolean>
        get() = _showStopBtn

    private val _showFilename = MutableLiveData<Boolean>()
    val showFilename: LiveData<Boolean>
        get() = _showFilename

    private val _filenameText = MutableLiveData<String>()
    val filenameText: LiveData<String>
        get() = _filenameText


    private val _recorderState = MutableLiveData<RecorderState>()
    val recorderState: LiveData<RecorderState>
        get() = _recorderState

    private var recorder: MediaRecorder? = null
    private var fileName: String = ""
    private var storageDir: String = ""

    val recorderStateObserver = Observer<RecorderState> {
        when (it) {
            RecorderState.NOTSTARTED -> {
                _recordButtonText.postValue("Record") //post value to the button
                showStopBtn(false)
            }
            RecorderState.RECORDING -> {
                _recordButtonText.postValue("Pause")
                showStopBtn(true)
            }
            RecorderState.PAUSED -> {
                _recordButtonText.postValue("Resume")
                showStopBtn(true)
            }
            RecorderState.ENDED -> {
                _recordButtonText.postValue("Play")
                showStopBtn(false)
                showFilenameEdittext(true)
            }
        }
    }


    private fun showStopBtn(show: Boolean) {
        _showStopBtn.postValue(show)
    }

    private fun showFilenameEdittext(show: Boolean) {
        _showFilename.postValue(show)
    }

    fun changeFilename() {

    }

    //initialise it with value
    init {
        recorderState.observeForever(recorderStateObserver)
        _recorderState.postValue(RecorderState.NOTSTARTED)
    }

    fun setStorageDir(sd: String) {
        storageDir = sd
    }

    fun setFilename(fn: String) {
        fileName = Utils().getDateTimeReadable()
        _filenameText.postValue(fn)
    }

    fun changeStoredFilename(fn: String) {
        //todo change on disk ond change in db
    }


    private fun getAbsoluteFilePath(): String {
        return storageDir + fileName + ".mp4"

    }

    fun onRecord() {
        when (recorderState.value) {
            RecorderState.NOTSTARTED -> {
                startRecording()
            }
            RecorderState.RECORDING -> {
                pauseRecording()
            }
            RecorderState.PAUSED -> {
                resumeRecording()
            }
            RecorderState.ENDED -> {
                //todo playRecording()
            }
        }
    }

    fun startRecording() {
        _recorderState.postValue(RecorderState.RECORDING)
        setFilename(Utils().getDateTimeReadable())
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.DEFAULT)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(getAbsoluteFilePath())
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

    fun resumeRecording() {
        _recorderState.postValue(RecorderState.RECORDING)
        recorder?.apply { resume() }
    }


    fun pauseRecording() {
        _recorderState.postValue(RecorderState.PAUSED)
        recorder?.apply {
            pause()
        }
    }


    fun stopRecording() {
        _recorderState.postValue(RecorderState.ENDED)
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

    override fun onCleared() {
        super.onCleared()
        recorder?.release()
        recorder = null
        recorderState.removeObserver(recorderStateObserver)
    }

}

