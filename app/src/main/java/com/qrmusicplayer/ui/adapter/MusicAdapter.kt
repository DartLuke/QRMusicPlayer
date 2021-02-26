package com.qrmusicplayer.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qrmusicplayer.R
import com.qrmusicplayer.model.Music
import kotlinx.android.synthetic.main.item_view.view.*

class MusicAdapter(
    private val musicList: ArrayList<Music>,
    private var onClickListener: OnClickListener
) :
    RecyclerView.Adapter<MusicAdapter.MyViewHolder>() {
    //private val musicSet:Set<>
    private var sortByAreaDescent: Boolean = false

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(music: Music, onClickListener: OnClickListener) {
            itemView.apply {
                item_music_name.text = music.name
                item_file_name.text = music.fileName

                itemView.setOnClickListener { onClickListener.onClickItem(music) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
        MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false))


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(musicList[position], onClickListener)
    }

    override fun getItemCount(): Int = musicList.size

    fun changeData(musicList: List<Music>) {
        Log.v("Test", musicList.toString())
        this.musicList.apply {
            clear()
            addAll(musicList)
        }
        Log.v("Test", this.musicList.toString())
        notifyDataSetChanged()
    }


    interface OnClickListener {
        fun onClickItem(music: Music)
    }
}