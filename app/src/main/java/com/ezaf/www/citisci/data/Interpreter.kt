package com.ezaf.www.citisci.data

import com.ezaf.www.citisci.MainActivity
import com.ezaf.www.citisci.utils.Logger.log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.time.Instant
import com.ezaf.www.citisci.utils.VerboseLevel.*

object Interpreter {

    var tasks = mutableListOf<ScriptRunner>()

    fun playScripts(expId: String) {
        //TODO: playScripts() implemeted this - what happens on multiple experiments? does this thread hold the lock?
        var fn = Throwable().stackTrace[0].methodName
        log(INFO, "$fn: called.")

        Observable.fromCallable {
            MainActivity.db?.experimentDao()?.getExpById(expId)!!
        }.doOnNext {
            var re = Regex("\\[|\\]")
            var str = re.replace(it.toString(),"")

            var actionList = it.expScript.expActions
            var condList = it.expScript.expConditions

            log(INFO,str)

        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()

    }

    fun stopScripts(expId: String) {
        //TODO: playScripts() implemeted this - what happens on multiple experiments? does this thread hold the lock?
        var fn = Throwable().stackTrace[0].methodName
        log(INFO_ERR, "$fn: called.")
    }

    private fun playScriptFor(action: ExpAction, condList: MutableList<ExpCondition>, startTime: Instant) {
        var fn = Throwable().stackTrace[0].methodName
        log(INFO_ERR, "$fn: called.")

        when(action.sensorType){
            SensorType.GPS-> {
                //add new task to the list and run the script
                tasks.add(ScriptRunner(action, condList.filter { it is GpsExpCondition } as MutableList<GpsExpCondition>, startTime))
                tasks[tasks.lastIndex].run()
            }
            SensorType.Camera-> return TODO("implement CameraExpCondition and invoke playCameraScript() from ScripRunner")
            SensorType.Michrophone-> return TODO("implement MicExpCondition and invoke playCameraScript() from ScripRunner")
            SensorType.Unknown -> return
        }
    }

}