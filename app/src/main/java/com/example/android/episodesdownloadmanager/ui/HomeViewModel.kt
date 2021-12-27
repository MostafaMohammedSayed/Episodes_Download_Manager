package com.example.android.episodesdownloadmanager.ui

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.android.episodesdownloadmanager.utils.debug

class HomeViewModel : ViewModel() {
    private val downloadButtonClickedLiveData = MutableLiveData<Boolean?>()
    fun uponDownloadClicked() {
        downloadButtonClickedLiveData.value = true
        debug(downloadButtonClickedLiveData.value)
    }

    fun resetDownloadClickedLiveData() {
        downloadButtonClickedLiveData.value = false
        debug(downloadButtonClickedLiveData.value)
    }

    fun observe(owner: LifecycleOwner, observer: Observer<Boolean?>) {
        downloadButtonClickedLiveData.observe(owner, observer)
    }
}