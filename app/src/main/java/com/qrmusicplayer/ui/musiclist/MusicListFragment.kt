package com.qrmusicplayer.ui.musiclist

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.qrmusicplayer.R
import com.qrmusicplayer.ui.qrscannerframent.QrScanerViewModel
import kotlinx.android.synthetic.main.fragment_music_list.view.*


class MusicListFragment : Fragment() {
    private lateinit var viewModel:MusicListModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       val view =inflater.inflate(R.layout.fragment_music_list, container, false)


        setupViewModel()
        setupObservers()
        getJson()
        return view
    }

    private fun setupObservers() {
        viewModel.jsonDownloaded.observe(viewLifecycleOwner, {
            if(it)
                viewModel.downloadFile()
        })
    }

    private fun getJson() {
        val safeArgs: MusicListFragmentArgs by navArgs()
        val url = safeArgs.url
        viewModel.getJson(url)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(MusicListModel::class.java)
    }


}