package com.qrmusicplayer.ui.qrscannerframent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections


class QrScanerViewModel : ViewModel() {

    val navigation: LiveData<NavDirections?> get() = _navigation
    private val _navigation = MutableLiveData<NavDirections?>()
    fun openMusicList(url: String) {
        _navigation.value =
            QrScannerFragmentDirections.actionQrScannerFragmentToMusicListFragment(url)
    }

    fun cleanNavigation()
    {
        _navigation.value=null
    }

}