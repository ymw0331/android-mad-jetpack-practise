package com.wayneyong.audiorecorderpro

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class PlaybackFragment : Fragment() {

    companion object {
        fun newInstance() = PlaybackFragment()
    }

    private lateinit var viewModel: PlaybackViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.playback_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PlaybackViewModel::class.java)  //create or retrive view model
        // TODO: Use the ViewModel


    }

}