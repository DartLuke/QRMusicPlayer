package com.qrmusicplayer.ui.musiclist

import android.os.Environment
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qrmusicplayer.api.RetrofitBuilder
import com.qrmusicplayer.model.Music
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import java.io.InputStream
import java.io.OutputStream

class MusicListModel : ViewModel() {
    lateinit var musicList: List<Music>
    val errorMessage = MutableLiveData<String>()
    val jsonDownloaded = MutableLiveData<Boolean>()
    fun getJson(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                musicList = RetrofitBuilder.api.getJson(url)
                Log.v("Test", musicList.toString())
                jsonDownloaded.postValue(true)
            } catch (exp: Exception) {
//                errorMessage.postValue("Error: $errorMessage")
//                Log.e("Test", exp.message)
                throw exp
            }
        }
    }

    fun downloadFile() {
        var url = musicList[0].url
        Log.v("TEST", "URL IS" + url)
        url = "https://www2.cs.uic.edu/~i101/SoundFiles/BabyElephantWalk60.wav"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.v("Test", "Start to download")
            for(i in musicList.indices) {
               // val body = RetrofitBuilder.apiDownload.downloadFile(musicList[i].url).body()

val response = RetrofitBuilder.apiDownload.downloadFile(musicList[i].url)
                var body = response.body()
                if (
                response.isSuccessful &&
                    body != null) {
                    Log.v("Test", "Start to download succeses ")
                    writeFile(body, i)
                }
            }
            } catch (exp: Exception) {
                errorMessage.postValue("Error: $errorMessage")
                   Log.e("Test", exp.message)
            }
        }
    }


    private fun writeFile(responseBody: ResponseBody, i:Int) {
  val fileName=musicList[i].url

        val pathName1 = "${Environment.getExternalStorageDirectory().path}/download/${
            fileName.substringAfterLast(
                "/"
            )
        }"

        val pathName = Environment.getExternalStorageDirectory().path+"/download/"+musicList[i].name+
                musicList[i].url.substringAfterLast("/")


        val file = File(pathName)

        file.createNewFile()
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            inputStream = responseBody.byteStream()
            outputStream = file.outputStream()

            //Total file length
            val contentLength = responseBody.contentLength()
            //Currently downloaded length
            var currentLength = 0L

            val buff = ByteArray(1024)
            var len = inputStream.read(buff)
            var percent = 0L
            while (len != -1) {
                outputStream.write(buff, 0, len)
                currentLength += len

                if ((currentLength * 100 / contentLength).toInt() > percent) {
                    percent = 100*(currentLength / contentLength)

                    Log.v("Test", pathName+ "downloading: $currentLength")

                }
                //прогресс бар
                /*Don't call the switch thread frequently, otherwise some mobile phones may cause stalls due to frequent thread switching.
                  Here is a restriction. Only when the download percentage is updated, will the thread be switched to update the UI*/
                /*  if((currentLength * 100/ contentLength).toInt()>percent){
                     percent = (currentLength / contentLength * 100).toInt()
                     //Switch to the main thread to update the UI
                     withContext(Dispatchers.Main) {
                         tv_download_state.text = "downloading:$currentLength / $contentLength"
                     }
                     //Switch back to the IO thread immediately after updating the UI
                 }
 */
                len = inputStream.read(buff)

            }
            musicList[i].pathway=pathName
        } catch (exp: Exception) {

        } finally {
            inputStream?.close()
            outputStream?.close()

        }

        Log.v("Test", musicList.toString())
    }
}