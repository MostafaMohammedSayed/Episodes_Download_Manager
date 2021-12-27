package com.example.android.episodesdownloadmanager.network

import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming
import retrofit2.http.Url

interface RetrofitService {
    @Streaming
    @GET
    fun downloadFile(@Url url:String): Observable<Response<ResponseBody>>
}