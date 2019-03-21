package com.ezaf.www.citisci.data

import com.ezaf.www.citisci.utils.Logger.log
import com.ezaf.www.citisci.utils.VerboseLevel.*
import java.time.Instant

class ScriptRunner(
        val action: ExpAction,
        val conds: List<GpsExpCondition>,
        val startTime: Instant
) : Runnable {

    override fun run() {
        val fn = object{}.javaClass.enclosingMethod.name
        log(INFO,"$fn: called. [sensorType = ${action.sensorType}]")

        when(action.sensorType){
            SensorType.GPS-> playGpsScript(action,conds,startTime)
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
            action.run {
                if(expDurationHasEnded(startTime) || allSamplesWereCollected()){
                    //TODO: endExperiment(): implement
                    //endExperiment()
                }
                else{
                    //collectData()
                }
            }
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