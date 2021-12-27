package com.example.android.episodesdownloadmanager.ui

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.android.episodesdownloadmanager.R
import com.example.android.episodesdownloadmanager.downloadService.DownloadService
import com.example.android.episodesdownloadmanager.downloadService.ServiceConstants.EPISODE_TITLE
import com.example.android.episodesdownloadmanager.downloadService.ServiceConstants.EPISODE_URL
import com.example.android.episodesdownloadmanager.episodesAdapter.DownloadClickListener
import com.example.android.episodesdownloadmanager.episodesAdapter.EpisodesAdapter
import com.example.android.episodesdownloadmanager.models.Episode
import com.example.android.episodesdownloadmanager.utils.debug
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity() {
    private val episodes = arrayListOf(
        Episode(
            1,
            "First Episode",
            "https://www.learningcontainer.com/download/sample-mp4-files-for-download/?wpdmdl=2556&refresh=61a88f25ef0d01638436645"
        ),
        Episode(
            2,
            "Second Episode",
            "https://www.learningcontainer.com/download/sample-mp4-file/?wpdmdl=2518&refresh=61a88f2606a2a1638436646"
        ),
        Episode(
            3,
            "Third Episode",
            "https://www.learningcontainer.com/download/sample-avi-files-for-testing/?wpdmdl=2582&refresh=61aca0d6e86651638703318"
        ),
        Episode(
            4,
            "Fourth Episode",
            "https://www.learningcontainer.com/download/sample-avi-video-files-for-testing/?wpdmdl=2608&refresh=619ea650e39d71637787216"
        ),
        Episode(
            5,
            "Fifth Episode",
            "https://www.learningcontainer.com/download/sample-mp4-video-file/?wpdmdl=2516&refresh=61a88f260d4cc1638436646"
        ),
        Episode(
            6,
            "Sixth Episode",
            "https://www.learningcontainer.com/download/sample-video-file-for-testing/?wpdmdl=2514&refresh=61a88f2614a5e1638436646"
        ),
        Episode(
            7,
            "Seventh Episode",
            "https://www.learningcontainer.com/download/sample-mpg-video-file-download/?wpdmdl=2642&refresh=61abbf18e5b311638645528"
        ),
        Episode(
            8,
            "Eighth Episode",
            "https://www.learningcontainer.com/download/sample-mpg-file/?wpdmdl=2647&refresh=61abbf18ecb3b1638645528"
        ),
    )
    private val downloadService = DownloadService()
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var episodesAdapter: EpisodesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setUpViews()
        setUpObservers()
    }

    private fun setUpObservers() {
        homeViewModel.observe(this, { bool ->
            if (bool == true) {
                homeViewModel.resetDownloadClickedLiveData()
            }
        })
    }

    private fun setUpViews() {
        episodesAdapter = EpisodesAdapter(DownloadClickListener { episode ->
            homeViewModel.uponDownloadClicked()
            val intent = Intent(this, downloadService.javaClass).apply {
                putExtra(EPISODE_URL, episode.url)
                putExtra(EPISODE_TITLE, episode.title)
            }
            if (!isMyServiceRunning(downloadService.javaClass)) {
                startService(intent)
            }
        })
        episodesAdapter.submitList(episodes)
        rvEpisodes.adapter = episodesAdapter
        val divider = DividerItemDecoration(
            rvEpisodes.context, RecyclerView.VERTICAL
        )
        rvEpisodes.addItemDecoration(divider)
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                debug("Service Running")
                return true
            }
        }
        debug("Service Not running")
        return false
    }

    /* override fun onDestroy() {
         val broadcastIntent = Intent("com.android.ServiceStopped")
         broadcastIntent.setClass(this, RestartBroadcastReceiver::class.java)
         this.sendBroadcast(broadcastIntent)
         super.onDestroy()
     }
     */
}