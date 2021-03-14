package com.qrmusicplayer.ui.musiclist

import android.app.Application
import android.media.MediaPlayer
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.qrmusicplayer.api.RetrofitBuilder
import com.qrmusicplayer.model.Music
import kotlinx.coroutines.*

import okhttp3.ResponseBody
import java.io.File
import java.io.InputStream
import java.io.OutputStream

class MusicListViewModel(application: Application) : AndroidViewModel(application) {


    val errorMessage: LiveData<String> get()=_errorMessage
    private var _errorMessage = MutableLiveData<String>()

     val isLoading : LiveData<Boolean> get()=_isLoading
    private val _isLoading = MutableLiveData<Boolean>()

    val musicList : LiveData<List<Music>> get()=musicListLiveData
    private  val musicListLiveData = MutableLiveData<List<Music>>()
    lateinit var _musicList: MutableList<Music>

    val errorLoading : LiveData<Boolean> get()=_errorLoading
    private  val _errorLoading = MutableLiveData<Boolean>()


    private val context = getApplication<Application>().applicationContext
    private var mediaPlayer: MediaPlayer = MediaPlayer()

    fun getJson(url: String) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _musicList = RetrofitBuilder.api.getJson(url) as MutableList<Music>
                _isLoading.postValue(false)
                downloadAllFiles()
            } catch (exp: Exception) {
                _isLoading.postValue(false)
                _errorMessage.postValue("Link is incorect Error: $_errorMessage")
                _errorLoading.postValue(true)
                exp.message?.let { Log.e("Test", it) }
            }
        }
    }

    private fun downloadAllFiles() {
        cleanFromDublicates()
        viewModelScope.launch(Dispatchers.IO) {
            coroutineScope {
                try {
                    var list: MutableList<Deferred<Unit>> = mutableListOf()
                    Log.v("Test", "***Download started***")
                    for (i in _musicList.indices) {
                        list.add(async {
                            try {
                                downloadfile(i)
                            } catch (exp: Exception) {
                                _errorMessage.postValue("Error: $_errorMessage")
                              //  Log.e("Test", exp.message)
                            }
                        })
                    }
                    list.awaitAll()
                    Log.v("Test", "***Download complete***")
                    _isLoading.postValue(false)
                    cleanListFromEmptyFiles()
                    musicListLiveData.postValue(_musicList)
                } catch (exp: Exception) {
                    _errorMessage.postValue("Error: $_errorMessage")
                    _isLoading.postValue(false)
                    exp.message?.let { Log.e("Test", it) }
                }
            }
        }
    }

    private fun cleanListFromEmptyFiles() {
        _musicList =
            _musicList.filter { it.pathway.isNotEmpty() } as MutableList<Music>
    }

    private suspend fun downloadfile(i: Int) {
        _musicList[i].isLoading = true
        musicListLiveData.postValue(_musicList)

        val response = RetrofitBuilder.api.downloadFile(_musicList[i].url)
        var body = response.body()
        Log.v("Test", _musicList[i].url + "  Response: " + response.code().toString())
        if (response.code() == 200 && body != null)
            writeFile(body, i)

        _musicList[i].isLoading = false
        musicListLiveData.postValue(_musicList)

    }

    private fun cleanFromDublicates() {
        var setCountries: MutableSet<String> = mutableSetOf()
        _musicList =
            _musicList.filter { setCountries.add(it.url) } as MutableList<Music>
    }

    private fun writeFile(responseBody: ResponseBody, i: Int) {
        val fileName = _musicList[i].url.substringAfterLast("/")
        val pathName =
            Environment.getExternalStorageDirectory().path + "/download/" + fileName


        val file = File(pathName)
        file.createNewFile()
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            inputStream = responseBody.byteStream()
            outputStream = file.outputStream()

            //Total file length
            // val contentLength = responseBody.contentLength()
            //Currently downloaded length
            //  var currentLength = 0L

            val buff = ByteArray(1024)
            var len = inputStream.read(buff)
            // var percent = 0L
            while (len != -1) {
                outputStream.write(buff, 0, len)
                // currentLength += len
//
//                if ((currentLength * 100 / contentLength).toInt() > percent) {
//                    percent = 100 * (currentLength / contentLength)
//
//                    Log.v("Test", pathName + "downloading: $currentLength")
//
//                }

                len = inputStream.read(buff)

            }
            _musicList[i].pathway = pathName
            _musicList[i].fileName = fileName
        } catch (exp: Exception) {
            _errorMessage.postValue("Error: $_errorMessage")
            exp.message?.let { Log.e("Test", it) }

        } finally {
            inputStream?.close()
            outputStream?.close()
        }

    }

    fun playMusic(music: Music) {
        if (music.pathway.isEmpty()) return
        try {


        mediaPlayer.stop()
        if (music.isPlaying) {
            mediaPlayer = MediaPlayer.create(context, music.pathway.toUri())
            mediaPlayer.start()
        }

    }   catch(exp: Exception)
        {

        }
    }


}