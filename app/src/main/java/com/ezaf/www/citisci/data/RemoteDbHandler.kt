package com.ezaf.www.citisci.data

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.ezaf.www.citisci.data.RemoteDbHandler.MsgType.*
import com.ezaf.www.citisci.utils.Logger
import com.ezaf.www.citisci.utils.TypeConverterUtil
import com.ezaf.www.citisci.utils.VerboseLevel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.JsonObject
import android.R.id.message
import org.json.JSONObject
import org.json.JSONArray
import android.R.attr.name
import android.R.id
import com.ezaf.www.citisci.utils.ParserUtil
import java.lang.reflect.Type;








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


    fun sendMsg(msgType: MsgType,sample: ExpSample) {
        val fn = Throwable().stackTrace[0].methodName
        Logger.log(VerboseLevel.INFO, "$fn: called.")

        Observable.fromCallable {
            service.run {
                when(msgType){
                    SEND_GPS_SAMPLE, SEND_CAM_SAMPLE, SEND_MIC_SAMPLE ->putSample(sample.actionID, sample)
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
                    list = ParserUtil.jsonToExpList(response.body().toString())
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