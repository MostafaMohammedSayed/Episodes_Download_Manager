package com.example.android.episodesdownloadmanager.downloadService

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.android.episodesdownloadmanager.R
import com.example.android.episodesdownloadmanager.downloadService.ServiceConstants.EPISODE_TITLE
import com.example.android.episodesdownloadmanager.downloadService.ServiceConstants.EPISODE_URL
import com.example.android.episodesdownloadmanager.network.Retrofit.Companion.downloadApi
import com.example.android.episodesdownloadmanager.utils.debug
import com.example.android.episodesdownloadmanager.utils.debugError
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.ResponseBody
import java.io.FileOutputStream


class DownloadService : Service() {

    private var episodeUrl: String? = ""
    private var fileName: String? = ""

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            startMyOwnForeground()
        } else {
            startForeground(1, Notification())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        val notificationChannelId = getString(R.string.download_channel)
        val channelName = getString(R.string.Download_Service)
        val channel = NotificationChannel(
            notificationChannelId,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        channel.lightColor = Color.BLUE
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(channel)
        val notificationBuilder = NotificationCompat.Builder(this, notificationChannelId)
        val notification: Notification = notificationBuilder.setOngoing(true)
            .setContentTitle(getString(R.string.app_in_background))
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(2, notification)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        var totalSize: Long? = 0L
        var fileSizeDownloaded = 0L
        episodeUrl = intent?.extras?.getString(EPISODE_URL)
        fileName = intent?.extras?.getString(EPISODE_TITLE)
        val filePath = applicationContext.filesDir.absoluteFile.toString() + fileName

        if (episodeUrl != null) {
            downloadApi.downloadFile(episodeUrl!!)
                //.observeOn(AndroidSchedulers.mainThread())
                .flatMap { responseOfResponseBody ->
                    if (responseOfResponseBody.isSuccessful) {
                        val input = responseOfResponseBody.body()?.byteStream()
                        val fos = FileOutputStream(filePath)
                        fos.use { output ->
                            val buffer = ByteArray(4 * 1024) // or other buffer size
                            var read = 0
                            while (input?.read(buffer).also {
                                    if (it != null) {
                                        read = it
                                    }
                                } != -1) {
                                output.write(buffer, 0, read)
                                totalSize = responseOfResponseBody.body()?.contentLength()
                                fileSizeDownloaded += read.toLong()
                                debug("fileSizeDownloaded$fileSizeDownloaded")
                                debug("totalSize$totalSize")
                                val percentDownloaded =
                                    calculateProgress(totalSize, fileSizeDownloaded)
                                debug("percentDownloaded$percentDownloaded")
                                val intent = Intent("updateProgress")
                                intent.putExtra("progress", percentDownloaded.toInt())
                                intent.setClass(this, ProgressBroadcastReceiver::class.java)
                                sendBroadcast(intent)
                            }
                            output.flush()
                        }
                    }
                    return@flatMap Observable.just(responseOfResponseBody.body()!!)
                }.subscribe(object : Observer<ResponseBody> {
                    override fun onSubscribe(d: Disposable) {
                        debug("Subscribed")
                    }

                    override fun onNext(responseBody: ResponseBody) {
                        debug(totalSize.toString())
                        debug(fileSizeDownloaded.toString())
                    }

                    override fun onError(error: Throwable) {
                        debugError(error)
                    }

                    override fun onComplete() {
                        debug("Completed")
                    }

                })
        }

        return START_REDELIVER_INTENT
    }

    private fun calculateProgress(totalSize: Long?, downloadSize: Long): Long {
        return if (totalSize != null) {
            ((downloadSize / totalSize) * 100)
        } else {
            return 0
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val intent = Intent("com.android.ServiceStopped")
        intent.putExtra(EPISODE_URL, episodeUrl)
        intent.putExtra(EPISODE_TITLE, episodeUrl)
        intent.setClass(this, RestartBroadcastReceiver::class.java)
        sendBroadcast(intent)
        super.onTaskRemoved(rootIntent)
    }


//    override fun onDestroy() {
//        val intent = Intent("com.android.ServiceStopped")
//        intent.putExtra(EPISODE_URL, episodeUrl)
//        intent.putExtra(EPISODE_TITLE, episodeUrl)
//        intent.setClass(this, RestartBroadcastReceiver::class.java)
//        sendBroadcast(intent)
//        super.onDestroy()
//    }
}