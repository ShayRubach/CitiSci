package com.ezaf.www.citisci.data

import com.ezaf.www.citisci.MainActivity.Companion.localDbHandler
import com.ezaf.www.citisci.utils.Logger.log
import com.ezaf.www.citisci.utils.VerboseLevel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.ezaf.www.citisci.utils.VerboseLevel.*
import io.reactivex.disposables.CompositeDisposable
import java.time.Instant

object Interpreter {

//    private var tasks = mutableListOf<ScriptRunner>()
    var observablesManager =  CompositeDisposable()

    fun playScripts(expId: String) {
        var fn = Throwable().stackTrace[0].methodName
        log(INFO, "$fn: called.")

        //access the db using an async operation with Observable
        Observable.fromCallable {

        }.doOnNext{
            localDbHandler.experimentDao().getExpById(expId).run {
                log(INFO, "$fn: action list size = ${actions.size}.")
                for(a in actions){
                    playScriptFor(a,basicData.startTime)
                }
            }
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()

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
            SensorType.GPS-> {
                //add new task to the list and run the script
//                tasks.add(ScriptRunner(action, condList.filter { it is GpsExpCondition } as MutableList<GpsExpCondition>, startTime))
//                tasks[tasks.lastIndex].run()
                ScriptRunner(action, startTime).playScript()
            }
            SensorType.Camera-> return //TODO("implement CameraExpCondition and invoke playCameraScript() from ScripRunner")
            SensorType.Michrophone-> return //TODO("implement MicExpCondition and invoke playCameraScript() from ScripRunner")
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