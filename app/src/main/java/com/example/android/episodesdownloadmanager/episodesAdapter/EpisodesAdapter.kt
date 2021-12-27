package com.example.android.episodesdownloadmanager.episodesAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.episodesdownloadmanager.R
import com.example.android.episodesdownloadmanager.downloadService.ProgressBroadcastReceiver
import com.example.android.episodesdownloadmanager.models.Episode
import com.example.android.episodesdownloadmanager.utils.debug
import com.google.android.material.progressindicator.LinearProgressIndicator

class EpisodesAdapter(private val onClickListener: DownloadClickListener) :
    ListAdapter<Episode, EpisodesAdapter.EpisodeViewHolder>(EpisodeDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        return EpisodeViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val episode = getItem(position)
        holder.bind(episode, onClickListener)
    }

    class EpisodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val progressReceiver = ProgressBroadcastReceiver()

        private val tvEpisodeTitle = itemView.findViewById<TextView>(R.id.episodeTitle)
        private val tvProgress = itemView.findViewById<TextView>(R.id.progress)
        private val ivDownloadButton = itemView.findViewById<ImageButton>(R.id.downloadButton)
        private val vDownloading = itemView.findViewById<ProgressBar>(R.id.downloading)
        private val ivDoneDownloading = itemView.findViewById<ImageView>(R.id.doneDownloading)
        private val tvPendingDownload = itemView.findViewById<TextView>(R.id.pendingDownload)
        private val vProgressBar =
            itemView.findViewById<LinearProgressIndicator>(R.id.downloadProgress)

        fun bind(episode: Episode, clickListener: DownloadClickListener) {
            tvEpisodeTitle.text = episode.title
            ivDownloadButton.setOnClickListener {
                clickListener.onClick(episode)
                if (isItemDownloading == null) {
                    ivDownloadButton.visibility = View.GONE
                    vProgressBar.visibility = View.VISIBLE
                    vDownloading.visibility = View.VISIBLE
                    debug("AdapterProgress=${progressReceiver.downloadProgress}")
                    progressReceiver.downloadProgress?.let { vProgressBar.progress = it }
                    isItemDownloading = true
                }else if (isItemDownloading == true){
                    ivDownloadButton.visibility = View.GONE
                    vDownloading.visibility = View.VISIBLE
                    tvPendingDownload.visibility = View.VISIBLE
                }

            }
        }

        companion object {
            fun from(parent: ViewGroup): EpisodeViewHolder {
                val inflatedView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_episode, parent, false)
                return EpisodeViewHolder(inflatedView)
            }
        }
    }

    class EpisodeDiffUtil : DiffUtil.ItemCallback<Episode>() {
        override fun areItemsTheSame(oldItem: Episode, newItem: Episode): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Episode, newItem: Episode): Boolean {
            return oldItem.id == newItem.id
        }
    }

    companion object{
        private var isItemDownloading: Boolean? = null
    }
}