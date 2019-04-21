package com.ezaf.www.citisci.utils

import android.location.Location
import com.ezaf.www.citisci.data.*
import com.ezaf.www.citisci.data.exp.ExpAction
import com.ezaf.www.citisci.data.exp.ExpSample
import com.ezaf.www.citisci.data.conds.GpsExpCondition
import com.ezaf.www.citisci.data.exp.ExpSampleList
import com.ezaf.www.citisci.data.exp.LatLong
import com.ezaf.www.citisci.utils.Logger.log
import com.ezaf.www.citisci.utils.VerboseLevel.*
import com.ezaf.www.citisci.utils.db.RemoteDbHandler
import com.ezaf.www.citisci.utils.db.RemoteDbHandler.MsgType.*

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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
            SensorType.Camera -> return
            SensorType.Michrophone -> return
            SensorType.Unknown -> return
        }
    }

    /**
     *  called every n (interval) seconds and get the current GPS coordinate
     */
    private fun playGpsScript(action: ExpAction, startTime: Instant) {
        val fn = Throwable().stackTrace[0].methodName
        log(INFO,"$fn: called. [sensorType = ${action.sensorType}]")

        val condCheck: (GpsExpCondition) -> Boolean = { it.isConditionMet() }
        val conds = GpsExpCondition.toExpConditionList(action.conditions)
        if(!conds.isEmpty() && conds.all(condCheck)){
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
                    RemoteDbHandler.sendMsg(SEND_GPS_SAMPLE, sampleList)
                    updateSamplesStatus()
                    }
                    else {
                        log(INFO,"$fn: interval halt time yet not over. retrying again in ${action.captureInterval} seconds")
                    }

                }
        }
        else{
            log(INFO,"$fn: !conds.isEmpty() =  ${!conds.isEmpty()}")
            log(INFO,"$fn: conds.all(condCheck) =  ${conds.all(condCheck)}")
            log(INFO,"$fn: conditions did not meet. retrying again in ${action.captureInterval} seconds")
        }

    }
//
//    private fun playCameraScript(){
//        var fn = Throwable().stackTrace[0].methodName
//        log(INFO_ERR, "$fn: called.")
//    }
//    private fun playMicScript(){
//        var fn = Throwable().stackTrace[0].methodName
//        log(INFO_ERR, "$fn: called.")
//    }
}