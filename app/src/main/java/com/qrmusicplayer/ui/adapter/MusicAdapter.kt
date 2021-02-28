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
   private var currentPlayingMusic:Music= Music()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(music: Music, onClickListener: OnClickListener, position: Int) {
            itemView.apply {
                itemView.setOnClickListener {
                    if(!music.isPlaying) {
                        currentPlayingMusic.isPlaying=false
                        music.isPlaying = true
                        currentPlayingMusic = music
                    }
                    else music.isPlaying =false
                    notifyDataSetChanged()
                    onClickListener.onClickItem(music)
                }

                item_music_name.text = music.name
                item_file_name.text = music.fileName

                if(music.isLoading) item_progressBar.visibility=View.VISIBLE
                else item_progressBar.visibility=View.GONE

                if(music.isPlaying) item_imageView.visibility=View.VISIBLE
                else item_imageView.visibility=View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
        MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false))

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(musicList[position], onClickListener, position)
    }

    override fun getItemCount(): Int = musicList.size

    fun changeData(musicList: List<Music>) {
        this.musicList.apply {
            clear()
            addAll(musicList)
        }
        notifyDataSetChanged()
    }

    interface OnClickListener {
        fun onClickItem(music: Music)
    }
}