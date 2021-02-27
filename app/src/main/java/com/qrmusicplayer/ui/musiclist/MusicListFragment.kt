package com.qrmusicplayer.ui.musiclist

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.qrmusicplayer.R
import com.qrmusicplayer.model.Music
import com.qrmusicplayer.ui.adapter.MusicAdapter
import kotlinx.android.synthetic.main.fragment_music_list.*
import kotlinx.android.synthetic.main.fragment_music_list.view.*


class MusicListFragment : Fragment(), MusicAdapter.OnClickListener {
    private lateinit var viewModel: MusicListViewModel
    private lateinit var adapter: MusicAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_music_list, container, false)
        setupViewModel()
        view = setupUI(view)
        setupObservers()
        if (savedInstanceState == null) getJson()
        return view
    }

    private fun setupUI(view: View?): View? {
        if (view != null) {
            adapter = MusicAdapter(arrayListOf(), this)
            view.music_recyclerView.layoutManager = LinearLayoutManager(view.context)
            view.music_recyclerView.addItemDecoration(
                DividerItemDecoration(
                    view.music_recyclerView.context,
                    (view.music_recyclerView.layoutManager as LinearLayoutManager).orientation
                )
            )
            view.music_recyclerView.adapter = adapter
        }

        return view
    }

    private fun setupObservers() {

        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it)
                progressBar.visibility = View.VISIBLE
            else progressBar.visibility = View.GONE
        })
        viewModel.musicList.observe(viewLifecycleOwner, {
            Log.v("Test", "****changeListViewData***")
            changeListViewData(it)

        })
        viewModel.errorMessage.observe(viewLifecycleOwner, {
            Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
        })

    }

    private fun getJson() {
        val safeArgs: MusicListFragmentArgs by navArgs()

        val url = safeArgs.url
        viewModel.getJson(url)
    }

    private fun changeListViewData(music: List<Music>) {
        if (adapter != null) {
            Log.v("Test", "****changeListViewData inside***")
            adapter.changeData(music)
        }

    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(MusicListViewModel::class.java)
    }

    override fun onClickItem(music: Music) {
        adapter.notifyDataSetChanged()
        viewModel.playMusic(music)


    }


}