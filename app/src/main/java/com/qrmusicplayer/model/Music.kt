package com.qrmusicplayer.model

import com.google.gson.annotations.SerializedName

data class Music(

    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String,
    var pathway:String
)
