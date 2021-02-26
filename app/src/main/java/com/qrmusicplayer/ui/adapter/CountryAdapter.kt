package com.qrmusicplayer.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qrmusicplayer.R
import com.qrmusicplayer.model.Music
import kotlinx.android.synthetic.main.item_view.view.*

class CountryAdapter(
    private val musicList: ArrayList<Music>,
    private var onClickListener: OnClickListener
) :
    RecyclerView.Adapter<CountryAdapter.MyViewHolder>() {

    private var sortByAreaDescent: Boolean = false

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(music: Music, onClickListener: OnClickListener) {
            itemView.apply {

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