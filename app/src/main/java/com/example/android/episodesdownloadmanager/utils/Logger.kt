package com.example.android.episodesdownloadmanager.utils

import android.util.Log

fun Any.debug(message: Any?){
    Log.d(this::class.java.simpleName,message.toString())
}

fun Any.debugError(message: Throwable?){
    Log.e(this::class.java.simpleName,message.toString(),message)
}