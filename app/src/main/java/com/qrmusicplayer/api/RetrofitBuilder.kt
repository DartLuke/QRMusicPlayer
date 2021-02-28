package com.qrmusicplayer.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitBuilder {

    private fun retrofit() =
        Retrofit.Builder().baseUrl("http://localhost/")
            .addConverterFactory(GsonConverterFactory.create()).build()

    val api: Api = retrofit().create(Api::class.java)


}