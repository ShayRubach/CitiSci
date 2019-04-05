package com.ezaf.www.citisci.utils.db

import com.ezaf.www.citisci.data.exp.ExpSample
import com.ezaf.www.citisci.data.exp.Experiment
import com.ezaf.www.citisci.utils.Logger
import com.ezaf.www.citisci.utils.ParserUtil
import com.ezaf.www.citisci.utils.VerboseLevel
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.ezaf.www.citisci.utils.db.RemoteDbHandler.MsgType.*
import com.ezaf.www.citisci.utils.service.HerokuService
import com.google.gson.JsonElement
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


    fun sendMsg(expId: String, msgType: MsgType, sample: ExpSample) {
        val fn = Throwable().stackTrace[0].methodName
        Logger.log(VerboseLevel.INFO, "$fn: called.")

        Observable.fromCallable {
            service.run {
                when(msgType){
                    SEND_GPS_SAMPLE, SEND_CAM_SAMPLE, SEND_MIC_SAMPLE ->putSample(expId, sample)
//                    SEND_GPS_SAMPLE, SEND_CAM_SAMPLE, SEND_MIC_SAMPLE ->putSample(sample.actionID, sample)
                    //SOME OTHER MSG TYPES HERE -> DO STUFF
                }
            }

        }.doOnNext{
            it.enqueue(object : Callback<ExpSample> {
                override fun onResponse(call: Call<ExpSample>, response: Response<ExpSample>) {
                    Logger.log(VerboseLevel.INFO, "$fn: $msgType successfully sent.")
                }

                override fun onFailure(call: Call<ExpSample>, t: Throwable) {
                    Logger.log(VerboseLevel.INFO, "$fn: failed to send $msgType.")
                }
            })
        }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    fun getAllExp() : List<Experiment> {
        val fn = Throwable().stackTrace[0].methodName
        Logger.log(VerboseLevel.INFO, "$fn: called.")
        var list: MutableList<Experiment> = mutableListOf()
            Observable.fromCallable {
            service.getAllExperiments()
        }.doOnNext{
            it.enqueue(object : Callback<JsonElement> {
                override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                    Logger.log(VerboseLevel.INFO, "$fn: got all experiments.")
                    ParserUtil.jsonToExpList(response.body().toString(), list)
                }



                override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                    Logger.log(VerboseLevel.INFO, "$fn: failed to get all experiments.")
                }
            })
        }
                .subscribeOn(Schedulers.io())
                .subscribe()
        return list
    }
}