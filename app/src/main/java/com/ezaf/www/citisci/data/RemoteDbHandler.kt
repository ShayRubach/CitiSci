package com.ezaf.www.citisci.data

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.ezaf.www.citisci.data.RemoteDbHandler.MsgType.*
import com.ezaf.www.citisci.utils.Logger
import com.ezaf.www.citisci.utils.VerboseLevel
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.converter.gson.GsonConverterFactory


object RemoteDbHandler
{
    enum class MsgType {
        SEND_GPS_SAMPLE,
        SEND_CAM_SAMPLE,
        SEND_MIC_SAMPLE
    }


    private const val baseUrl = "https://tempcitisci.herokuapp.com/"
    private var retrofit: Retrofit
    private var service: HerokuService


    init {
        retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        service = retrofit.create(HerokuService::class.java)
    }


    fun sendMsg(msgType: MsgType,jsonStr: String, expId: String = "") {
        val fn = object{}.javaClass.enclosingMethod.name
        Logger.log(VerboseLevel.INFO, "$fn: called.")

        val body = RequestBody.create(MediaType.parse("application/json"), jsonStr)

        Observable.fromCallable {
            service.putSample(expId, body)
        }.doOnNext{
            it.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Logger.log(VerboseLevel.INFO, "$fn: $msgType successfully sent.")
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Logger.log(VerboseLevel.INFO, "$fn: failed to send $msgType.")
                }
            })
        }
                .subscribeOn(Schedulers.io())
                .subscribe()

    }
}