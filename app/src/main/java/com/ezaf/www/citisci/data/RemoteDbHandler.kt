package com.ezaf.www.citisci.data

import com.ezaf.www.citisci.MainActivity
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
import com.google.gson.Gson
import com.google.gson.JsonElement
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
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
        val fn = Throwable().stackTrace[0].methodName
        Logger.log(VerboseLevel.INFO, "$fn: called.")

        val body = RequestBody.create(MediaType.parse("application/json"), jsonStr)
        Logger.log(VerboseLevel.INFO, "$fn: jsonStr=\n$jsonStr")

        Observable.fromCallable {
            service.run {
                when(msgType){
                    SEND_GPS_SAMPLE, SEND_CAM_SAMPLE, SEND_MIC_SAMPLE ->putSample(expId, jsonStr)
                    //SOME OTHER MSG TYPES HERE -> DO STUFF
                }
            }

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

    fun getAllExp() {
        val fn = Throwable().stackTrace[0].methodName
        Logger.log(VerboseLevel.INFO, "$fn: called.")
        var list: List<Experiment>
        Observable.fromCallable {
            service.getAllExperiments()
        }
                .doOnNext{
                    it.enqueue(object : Callback<JsonElement> {
                        override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                            Logger.log(VerboseLevel.INFO, "$fn: got all experiments.")
                            Logger.log(VerboseLevel.INFO, "$fn: body = \n${response.body().toString()}")
                        }

                        override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                            Logger.log(VerboseLevel.INFO, "$fn: failed to get all experiments.")
                        }
                    })
                }
                .subscribeOn(Schedulers.io())
                .subscribe()
        }
}