package com.example.android.episodesdownloadmanager.network

import com.example.android.episodesdownloadmanager.network.NetworkConstants.BASE_URL
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory

class Retrofit {
    companion object {
        private val retrofit: Retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .baseUrl(BASE_URL)
            .build()

        val downloadApi: RetrofitService by lazy {
                    retrofit.create(RetrofitService::class.java)
                }
    }
}