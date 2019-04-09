package com.ezaf.www.citisci.utils

import com.ezaf.www.citisci.data.exp.ExpAction
import com.ezaf.www.citisci.data.SensorType
import com.ezaf.www.citisci.data.exp.Experiment
import com.ezaf.www.citisci.utils.Logger.log
import com.ezaf.www.citisci.utils.VerboseLevel.*
import io.reactivex.disposables.CompositeDisposable
import java.time.Instant

object Interpreter {

    var observablesManager =  CompositeDisposable()

    fun playScriptList(expList: List<Experiment>){
        expList.forEach {
            if(it.basicData.automatic)
                playScripts(it)
        }
    }

    private fun playScripts(exp: Experiment) {
        exp.actions.forEach {
            playScriptFor(it, exp.basicData.startTime)
        }
    }

//    fun stopScript(expId: String) {
//        //TODO: stopScripts() implemeted this - what happens on multiple experiments? does this thread hold the lock?
//        var fn = Throwable().stackTrace[0].methodName
//        log(INFO_ERR, "$fn: called.")
//    }
//
    private fun playScriptFor(action: ExpAction, startTime: Instant) {
        var fn = Throwable().stackTrace[0].methodName
        log(INFO_ERR, "$fn: called.")

        when(action.sensorType){
            SensorType.GPS -> {
                //add new task to the list and run the script
//                tasks.add(ScriptRunner(action, condList.filter { it is GpsExpCondition } as MutableList<GpsExpCondition>, startTime))
//                tasks[tasks.lastIndex].run()
                ScriptRunner(action, startTime).playScript()
            }
            SensorType.Camera -> return //TODO("implement CameraExpCondition and invoke playCameraScript() from ScripRunner")
            SensorType.Michrophone -> return //TODO("implement MicExpCondition and invoke playCameraScript() from ScripRunner")
            SensorType.Unknown -> return
        }
    }
//
//    fun stopAllScripts() {
//        //TODO: playScripts() implemeted this - what happens on multiple experiments? does this thread hold the lock?
//        var fn = Throwable().stackTrace[0].methodName
//        log(INFO_ERR, "$fn: called.")
//
//        //dispose and stop all observables that are running the scripts
//        observablesManager.dispose()
//
//    }

}