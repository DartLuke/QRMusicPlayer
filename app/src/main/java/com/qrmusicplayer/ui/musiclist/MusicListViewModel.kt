package com.qrmusicplayer.ui.musiclist

import android.app.Application
import android.media.MediaPlayer
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
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
    lateinit var _musicList: MutableList<Music>
    val errorMessage = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>()
    var musicList = MutableLiveData<List<Music>>()


    private val context = getApplication<Application>().applicationContext
    private var mediaPlayer: MediaPlayer = MediaPlayer()

    //   val jsonDownloaded = MutableLiveData<Boolean>()
    fun getJson(url: String) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _musicList = RetrofitBuilder.api.getJson(url) as MutableList<Music>
                isLoading.postValue(false)
                downloadAllFiles()
            } catch (exp: Exception) {
                isLoading.postValue(false)
                errorMessage.postValue("Error: $errorMessage")
                Log.e("Test", exp.message)
            }
        }
    }

    private fun downloadAllFiles() {
        cleanFromDublicates()
        viewModelScope.launch(Dispatchers.IO) {
            coroutineScope {
                try {
                    //  var setCountries: MutableSet<String> = mutableSetOf()
                    var list: MutableList<Deferred<Unit>> = mutableListOf()
                    Log.v("Test", "Start to download")
                    for (i in _musicList.indices) {
                        list.add(async {
                            _musicList[i].isLoading = true
                            musicList.postValue(_musicList)
                            downloadfile(i)
                            _musicList[i].isLoading = false
                            musicList.postValue(_musicList)
                        })
                    }
                    list.awaitAll()
                    Log.v("Test", "****DownLoad is finished***")
                    isLoading.postValue(false)
                    cleanList()
                    musicList.postValue(_musicList)
                } catch (exp: Exception) {
                    errorMessage.postValue("Error: $errorMessage")
                    isLoading.postValue(false)
                    Log.e("Test", exp.message)
                }
            }
        }
    }

    private fun cleanList() {
        _musicList =
            _musicList.filter { it.fileName.isNotEmpty() } as MutableList<Music>

    }

    private suspend fun downloadfile(i: Int) {
        musicList.postValue(_musicList)
        val response = RetrofitBuilder.apiDownload.downloadFile(_musicList[i].url)
        var body = response.body()
        Log.v("Test", "Response is " + response.code().toString() + "for " + _musicList[i].name)
        if (response.code() == 200 && body != null)
            writeFile(body, i)


    }

    private fun cleanFromDublicates() {
        var setCountries: MutableSet<String> = mutableSetOf()
        _musicList =
            _musicList.filter { setCountries.add(it.url.substringAfterLast("/")) } as MutableList<Music>
        Log.v("Test", _musicList.toString())
    }

    private fun writeFile(responseBody: ResponseBody, i: Int) {
        Log.v("Test", "Started to write file")
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
            errorMessage.postValue("Error: $errorMessage")
            Log.e("Test", exp.message)

        } finally {
            inputStream?.close()
            outputStream?.close()
        }

    }

    fun playMusic(music: Music) {

        mediaPlayer.stop()
        if (music.isPlaying) {
            mediaPlayer = MediaPlayer.create(context, music.pathway.toUri())

            mediaPlayer.start()
        }
    }


}