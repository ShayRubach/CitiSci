package com.ezaf.www.citisci.data

import com.ezaf.www.citisci.utils.Logger.log
import com.ezaf.www.citisci.utils.VerboseLevel.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.time.Instant
import java.util.concurrent.TimeUnit

class ScriptRunner(
        private val action: ExpAction,
        private val conds: List<GpsExpCondition>,
        private val startTime: Instant
) {

    fun collect() {
        val fn = object{}.javaClass.enclosingMethod.name
        log(INFO,"$fn: called. [sensorType = ${action.sensorType}]")

        when(action.sensorType){
            SensorType.GPS-> {

                Interpreter.observablesManager.add(
                        Observable.interval(action.captureInterval.toLong(), TimeUnit.SECONDS)
                                .timeInterval()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe{
                                    playGpsScript(action, conds, startTime)
                                }
                )

            }
            SensorType.Camera-> return
            SensorType.Michrophone-> return
            SensorType.Unknown -> return
        }
    }

    private fun playGpsScript(action: ExpAction, conds: List<GpsExpCondition>, startTime: Instant) {
        val fn = object{}.javaClass.enclosingMethod.name
        log(INFO,"$fn: called. [sensorType = ${action.sensorType}]")

        val condCheck: (GpsExpCondition) -> Boolean = { it.isConditionMet() }
        if(conds.all(condCheck)){
            log(INFO,"$fn: conditions met.")
            action.run {
                if(expDurationHasEnded(startTime) || allSamplesWereCollected()){
                    log(INFO,"$fn: experiment duration has ended or all samples were collected. ending experiment")
                    //TODO: endExperiment(): implement
                    //endExperiment()
                }
                else{
                    log(INFO,"$fn: calling data collector")
                    //collectData()
                }
            }
        }
        else{
            log(INFO,"$fn: conditions did not met. retrying again in ${action.captureInterval} seconds")
        }

    }

    private fun playCameraScript(){
        var fn = Throwable().stackTrace[0].methodName
        log(INFO_ERR, "$fn: called.")
    }
    private fun playMicScript(){
        var fn = Throwable().stackTrace[0].methodName
        log(INFO_ERR, "$fn: called.")
    }
}