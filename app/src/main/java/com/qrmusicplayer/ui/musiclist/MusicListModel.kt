package com.qrmusicplayer.ui.musiclist

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qrmusicplayer.api.RetrofitBuilder
import com.qrmusicplayer.model.Music
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import okhttp3.ResponseBody
import retrofit2.http.HTTP
import java.io.File
import java.io.InputStream
import java.io.OutputStream

class MusicListModel : ViewModel() {
    lateinit var _musicList: MutableList<Music>
    val errorMessage = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>(false)
    var musicList = MutableLiveData<List<Music>>()

    //   val jsonDownloaded = MutableLiveData<Boolean>()
    fun getJson(url: String) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _musicList = RetrofitBuilder.api.getJson(url) as MutableList<Music>
                Log.v("Test", _musicList.toString())
                //   jsonDownloaded.postValue(true)
                downloadFile()
            } catch (exp: Exception) {
                errorMessage.postValue("Error: $errorMessage")
                isLoading.value = false
                Log.e("Test", exp.message)
            }
        }

    }

    private fun downloadFile() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var setCountries: MutableSet<String> = mutableSetOf()

                Log.v("Test", "Start to download")
                for (i in _musicList.indices.reversed()) {
                    val fileName = _musicList[i].url.substringAfterLast("/")
                    if (setCountries.contains(fileName)) {
                        //don't download file with same name
                        _musicList.removeAt(i)
                        continue
                    }
                    setCountries.add(fileName)
                    val response = RetrofitBuilder.apiDownload.downloadFile(_musicList[i].url)
                    var body = response.body()
                    Log.v(
                        "Test",
                        "Response is " + response.code().toString() + "for " + _musicList[i].name
                    )
                    if (
                        response.code() == 200 &&
                        body != null
                    ) {
                        Log.v("Test", "Start to write file ")
                        writeFile(body, i)
                    } else {
                        _musicList.removeAt(i)

                    }
                }
                Log.v("Test", "****DownLoad is finished***")
                isLoading.postValue(false)
                musicList.postValue(_musicList)
            } catch (exp: Exception) {
                errorMessage.postValue("Error: $errorMessage")
                isLoading.postValue(false)
                Log.e("Test", exp.message)
            }
        }
    }

    private fun writeFile(responseBody: ResponseBody, i: Int) {
        val fileName = _musicList[i].url.substringAfterLast("/")

        val pathName1 = "${Environment.getExternalStorageDirectory().path}/download/${
            fileName.substringAfterLast(
                "/"
            )
        }"

        val pathName =
            Environment.getExternalStorageDirectory().path + "/download/" + _musicList[i].name + "_" + fileName


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
        var player: MediaPlayer
        val mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            //    setDataSource(applicationContext, music.pathway.toUri())
            prepare()
            start()
        }


    }


}