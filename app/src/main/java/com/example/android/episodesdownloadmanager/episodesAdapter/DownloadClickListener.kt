package com.example.android.episodesdownloadmanager.episodesAdapter

import com.example.android.episodesdownloadmanager.models.Episode

class DownloadClickListener(val clickListener: (episode: Episode) -> Unit){
    fun onClick(episode: Episode) = clickListener(episode)
}