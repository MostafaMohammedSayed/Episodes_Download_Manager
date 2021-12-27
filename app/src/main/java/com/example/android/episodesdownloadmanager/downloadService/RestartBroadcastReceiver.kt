package com.example.android.episodesdownloadmanager.downloadService

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.example.android.episodesdownloadmanager.downloadService.ServiceConstants.EPISODE_TITLE
import com.example.android.episodesdownloadmanager.downloadService.ServiceConstants.EPISODE_URL
import com.example.android.episodesdownloadmanager.utils.debug

class RestartBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "com.android.ServiceStopped"){
            Toast.makeText(context, "Restarter begins", Toast.LENGTH_LONG).show()
            val episodeUrl = intent?.extras?.getString(EPISODE_URL)
            val episodeTitle = intent?.extras?.getString(EPISODE_TITLE)
            val newIntent = Intent(context, DownloadService::class.java)
                .apply {
                    putExtra(EPISODE_URL, episodeUrl)
                    putExtra(EPISODE_TITLE, episodeTitle)
                }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context?.startForegroundService(newIntent)
            } else {
                context?.startService(newIntent)
            }
        }
    }

}