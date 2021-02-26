package com.qrmusicplayer.api

import com.qrmusicplayer.model.Music
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface Api {

     @GET
     suspend fun getJson(@Url url:String) :List<Music>


}