package com.ezaf.www.citisci.utils.db

import android.widget.Toast
import com.ezaf.www.citisci.data.exp.ExpAction
import com.ezaf.www.citisci.data.exp.ExpSample
import com.ezaf.www.citisci.data.exp.Experiment
import com.ezaf.www.citisci.data.exp.SharedDataHelper
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
import io.reactivex.disposables.Disposable
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


    fun sendMsg(expId: String, msgType: MsgType, samples: List<ExpSample>) {
        val fn = Throwable().stackTrace[0].methodName
        Logger.log(VerboseLevel.INFO, "$fn: called.")

        Observable.fromCallable {
            service.run {
                when(msgType){
                    SEND_GPS_SAMPLE, SEND_CAM_SAMPLE, SEND_MIC_SAMPLE ->putSampleList(samples)
                    //SOME OTHER MSG TYPES HERE -> DO STUFF
                }
            }

        }.doOnNext{
            it.enqueue(object : Callback<List<ExpSample>> {
                override fun onResponse(call: Call<List<ExpSample>>, response: Response<List<ExpSample>>) {
                    Logger.log(VerboseLevel.INFO, "$fn: $msgType successfully sent.")
                }

                override fun onFailure(call: Call<List<ExpSample>>, t: Throwable) {
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
            service.getMyExperiments("email1002")
        }
    }

    fun joinExp(expId: String): Observable<Call<JsonElement>>{
        return Observable.fromCallable {
            service.joinExp(expId, "my_email")
        }
    }

}