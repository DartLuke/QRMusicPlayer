package com.qrmusicplayer.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitBuilder {


    var client = OkHttpClient.Builder()

        .retryOnConnectionFailure(true)
        .connectTimeout(15, TimeUnit.SECONDS)
        .build()
    private fun retrofit() =
        Retrofit.Builder().baseUrl("http://localhost/")
            .addConverterFactory(GsonConverterFactory.create()).build()

    private fun retrofitDownload() =
        Retrofit.Builder().baseUrl("http://localhost/").client(client).build()

    val api: Api = retrofit().create(Api::class.java)
    val apiDownload= retrofitDownload().create(ApiDownload::class.java)

}