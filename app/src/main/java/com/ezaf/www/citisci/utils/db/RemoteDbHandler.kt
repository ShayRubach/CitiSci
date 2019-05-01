package com.ezaf.www.citisci.utils.db

import com.ezaf.www.citisci.data.exp.*
import com.ezaf.www.citisci.utils.Logger
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
import com.ezaf.www.citisci.data.exp.EncodingType.*


object RemoteDbHandler
{
    enum class MsgType {
        SEND_GPS_SAMPLE,
        SEND_CAM_SAMPLE,
        SEND_MIC_SAMPLE,
        SEND_MAGNETIC_FIELD_SAMPLE
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


    fun sendMsg(msgType: MsgType, samples: ExpSampleList) {
        val fn = Throwable().stackTrace[0].methodName
        Logger.log(VerboseLevel.INFO, "$fn: called.")

        Observable.fromCallable {
            service.run {
                when(msgType){
                    SEND_GPS_SAMPLE, SEND_MIC_SAMPLE, SEND_MAGNETIC_FIELD_SAMPLE ->putSampleList(samples, REGULAR.toString())
                    SEND_CAM_SAMPLE -> putSampleList(samples, BASE64.toString())
                    //SOME OTHER MSG TYPES HERE -> DO STUFF
                }
            }

        }.doOnNext{
            it.enqueue(object : Callback<ExpSampleList> {
                override fun onResponse(call: Call<ExpSampleList>, response: Response<ExpSampleList>) {
                    Logger.log(VerboseLevel.INFO, "$fn: $msgType successfully sent.")
                }

                override fun onFailure(call: Call<ExpSampleList>, t: Throwable) {
                    Logger.log(VerboseLevel.INFO, "$fn: failed to send $msgType.")
                }
            })
        }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    fun getAllExp() : Observable<Call<JsonElement>> {
        val fn = Throwable().stackTrace[0].methodName
        Logger.log(VerboseLevel.INFO, "$fn: called.")

        return Observable.fromCallable {
            service.getAllExperiments()
        }
    }

    fun getMyExp() : Observable<Call<JsonElement>> {
        val fn = Throwable().stackTrace[0].methodName
        Logger.log(VerboseLevel.INFO, "$fn: called.")

        return Observable.fromCallable {
            service.getMyExperiments("participant@gmail.com")
        }
    }

    fun joinExp(expId: String): Observable<Call<JoinExpRequest>>{
        return Observable.fromCallable {
            service.joinExp(JoinExpRequest(expId),"participant@gmail.com")
        }
    }

}

class JoinExpRequest(private val id: String)