package com.ezaf.www.citisci.utils

import android.location.Location
import com.ezaf.www.citisci.data.*
import com.ezaf.www.citisci.data.exp.*
import com.ezaf.www.citisci.utils.Logger.log
import com.ezaf.www.citisci.utils.VerboseLevel.*
import com.ezaf.www.citisci.utils.db.RemoteDbHandler
import com.ezaf.www.citisci.utils.db.RemoteDbHandler.MsgType.*

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Instant
import java.util.concurrent.TimeUnit

class ScriptRunner(
        private val action: ExpAction,
        private val startTime: Instant
) {

    fun playScript() {
        val fn = Throwable().stackTrace[0].methodName
        log(INFO,"$fn: called. [sensorType = ${action.sensorType}]")

        when(action.sensorType){
            SensorType.GPS -> {

                Interpreter.observablesManager.add(
                        Observable.interval(action.captureInterval.toLong(), TimeUnit.SECONDS)
                                .timeInterval()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe{
                                    playGpsScript(action, startTime)
                                }
                )

            }
            SensorType.MAGNETIC_FIELD -> {
                Interpreter.observablesManager.add(
                        Observable.interval(action.captureInterval.toLong(), TimeUnit.SECONDS)
                                .timeInterval()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe{
                                    playMagneticFieldScript(action, startTime)
                                }
                )
            }

            else -> return
        }
    }

    private fun playMagneticFieldScript(action: ExpAction, startTime: Instant) {
        val fn = Throwable().stackTrace[0].methodName
        log(INFO,"$fn: called. [sensorType = ${action.sensorType}]")

        val condCheck: (ExpCondition) -> Boolean = { it.isConditionMet() }
        if(!action.condsList.isEmpty() && action.condsList.all(condCheck)){
            log(INFO,"$fn: conditions met.")
            action.run {
                /*if(expDurationHasEnded(startTime) || allSamplesWereCollected()){
                    log(INFO,"$fn: experiment duration has ended or all samples were collected. ending experiment")
                    //TODO: endExperiment(): implement
                    //endExperiment()
                }*/
                if(isIntervalPassedFromLastCapture()){
                    log(INFO,"$fn: calling data collector.")
                    var magneticFieldValues = DataCollector.collect(sensorType) as FloatArray

                    val sampleList = ExpSampleList()
                    val sample = ExpSample(action.expId, action._id, "participant@gmail.com",
                            MagneticFields(
                                    magneticFieldValues[0],
                                    magneticFieldValues[1],
                                    magneticFieldValues[2])
                    )
                    sampleList.addSample(sample)

                    val mf = sample.sample as MagneticFields

                    log(INFO,"$fn: mf =\n${mf.x},${mf.y},${mf.z} ")
                    RemoteDbHandler.sendMsg(SEND_MAGNETIC_FIELD_SAMPLE, sampleList).
                            doOnNext{
                                it.enqueue(object : Callback<ExpSampleList> {
                                    override fun onResponse(call: Call<ExpSampleList>, response: Response<ExpSampleList>) {
                                        Logger.log(VerboseLevel.INFO, "$fn: SEND_MAGNETIC_FIELD_SAMPLE successfully sent.")
                                        updateSamplesStatus()
                                    }

                                    override fun onFailure(call: Call<ExpSampleList>, t: Throwable) {
                                        Logger.log(VerboseLevel.INFO, "$fn: failed to send SEND_MAGNETIC_FIELD_SAMPLE.")
                                    }
                                })
                            }
                            .subscribeOn(Schedulers.io())
                            .subscribe()
                }
                else {
                    log(INFO,"$fn: interval halt time yet not over. retrying again in ${action.captureInterval} seconds")
                }

            }
        }
    }

    /**
     *  called every n (interval) seconds and get the current GPS coordinate
     */
    private fun playGpsScript(action: ExpAction, startTime: Instant) {
        val fn = Throwable().stackTrace[0].methodName
        log(INFO,"$fn: called. [sensorType = ${action.sensorType}]")

        val condCheck: (ExpCondition) -> Boolean = { it.isConditionMet() }
        if(!action.condsList.isEmpty() && action.condsList.all(condCheck)){
            log(INFO,"$fn: conditions met.")
            action.run {
                /*if(expDurationHasEnded(startTime) || allSamplesWereCollected()){
                    log(INFO,"$fn: experiment duration has ended or all samples were collected. ending experiment")
                    //TODO: endExperiment(): implement
                    //endExperiment()
                }*/
                if(isIntervalPassedFromLastCapture()){
                    log(INFO,"$fn: calling data collector.")
                    var location = DataCollector.collect(sensorType) as Location

                    val sampleList = ExpSampleList()
                    val sample = ExpSample(action.expId, action._id, "participant@gmail.com", LatLong(location.latitude, location.longitude))

                    sampleList.addSample(sample)

                    log(INFO,"$fn: samples=\n$sample")
                    RemoteDbHandler.sendMsg(SEND_GPS_SAMPLE, sampleList).
                            doOnNext{
                                it.enqueue(object : Callback<ExpSampleList> {
                                    override fun onResponse(call: Call<ExpSampleList>, response: Response<ExpSampleList>) {
                                        Logger.log(VerboseLevel.INFO, "$fn: SEND_GPS_SAMPLE successfully sent.")
                                        updateSamplesStatus()
                                    }

                                    override fun onFailure(call: Call<ExpSampleList>, t: Throwable) {
                                        Logger.log(VerboseLevel.INFO, "$fn: failed to send SEND_GPS_SAMPLE.")
                                    }
                                })
                            }
                            .subscribeOn(Schedulers.io())
                            .subscribe()
                }
                else {
                    log(INFO,"$fn: interval halt time yet not over. retrying again in ${action.captureInterval} seconds")
                }
            }
        }
    }

}