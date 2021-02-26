package com.qrmusicplayer.ui.qrscannerframent

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections

class QrScanerViewModel : ViewModel() {
     val navigation = MutableLiveData<NavDirections?>()
    fun openMusicList(url: String) {
     navigation.value=   QrScannerFragmentDirections.actionQrScannerFragmentToMusicListFragment(url)
    }
    fun navigationCompelete() {
        navigation.value = null
    }

}