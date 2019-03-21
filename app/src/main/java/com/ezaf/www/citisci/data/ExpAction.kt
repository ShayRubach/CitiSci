package com.ezaf.www.citisci.data

import com.ezaf.www.citisci.utils.VerboseLevel.*
import com.ezaf.www.citisci.utils.Logger.log
import java.time.Duration
import java.time.Instant

class ExpAction (
        val captureInterval: Double,
        val samplesToCollect: Int,
        val duration: Int,
        val sensorType: SensorType){

    private val TIME_DIVISOR = 3600.0
    private var lastTimeCollected = Instant.now()

    //TODO: add this to the typeconverter
    private var samplesCollected: Int = 0

    private fun updateSamplesStatus()  {
        var fn = Throwable().stackTrace[0].methodName
        log(INFO_ERR, "$fn: called.")

        lastTimeCollected = Instant.now()
        samplesCollected++
    }

    fun allSamplesWereCollected() = samplesCollected == samplesToCollect

    fun collectData(startTime: Instant) {
        var fn = Throwable().stackTrace[0].methodName
        log(INFO_ERR, "$fn: called.")

        val timePassedFromLatestCapture = Duration.between(lastTimeCollected, Instant.now()).seconds / TIME_DIVISOR

        if(timePassedFromLatestCapture >= captureInterval){
            DataCollector.collect(sensorType, startTime)
            updateSamplesStatus()
        }
    }

    override fun toString(): String {
        return  "$captureInterval|$samplesToCollect|$duration|$sensorType"
    }

    fun expDurationHasEnded(startTime: Instant): Boolean {
        var fn = Throwable().stackTrace[0].methodName
        log(INFO_ERR, "$fn: called.")

        val timeNow = Instant.now()
        val diff = Duration.between(startTime, timeNow)
        return (diff.seconds / TIME_DIVISOR) > duration
    }

}
