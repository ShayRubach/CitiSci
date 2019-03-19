package com.ezaf.www.citisci.data

import com.ezaf.www.citisci.MainActivity
import java.time.Instant

object Interpreter {

    fun playScripts(expId: String) {
        //TODO: playScripts() implemeted this - what happens on multiple experiments? does this thread hold the lock?
        var exp = MainActivity.db?.experimentDao()?.getExpById(expId)
        exp?.run {
            var actionList = expScript.expActions
            var condList = expScript.expConditions

            for (action in actionList){
                playScriptFor(action, condList, exp.expBasicData.startTime)
            }
        }
    }

    fun stopScripts(expId: String) {
        //TODO: playScripts() implemeted this - what happens on multiple experiments? does this thread hold the lock?
    }

    private fun playScriptFor(action: ExpAction, condList: MutableList<ExpCondition>, startTime: Instant){
        when(action.sensorType){
            SensorType.GPS-> playGpsScript(action, condList.filter { it is GpsExpCondition } as MutableList<GpsExpCondition>, startTime)
            SensorType.Camera-> playCameraScript()
            SensorType.Michrophone-> playMicScript()
            SensorType.Unknown -> return
        }
    }

    private fun playGpsScript(action: ExpAction, conds: List<GpsExpCondition>, startTime: Instant) {

        val condCheck: (GpsExpCondition) -> Boolean = { it.isConditionMet() }
        if(conds.all(condCheck)){
            action.run {
                if(expDurationHasEnded(startTime) || allSamplesWereCollected()){
                    //TODO: endExperiment(): implement
                    //endExperiment()
                }
                else{
                    collectData(startTime)
                }
            }
        }

    }

    private fun playCameraScript(){}
    private fun playMicScript(){}
}