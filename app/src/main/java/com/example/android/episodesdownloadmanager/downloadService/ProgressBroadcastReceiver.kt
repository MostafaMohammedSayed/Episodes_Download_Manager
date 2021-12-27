package com.example.android.episodesdownloadmanager.downloadService

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.android.episodesdownloadmanager.utils.debug

class ProgressBroadcastReceiver: BroadcastReceiver() {
    var downloadProgress: Int? = 0
    override fun onReceive(context: Context?, intent: Intent?) {
        debug("CALLED")
        if (intent?.action=="updateProgress"){
            downloadProgress = intent.extras?.getInt("progress")
        }
        debug(downloadProgress)
    }
}