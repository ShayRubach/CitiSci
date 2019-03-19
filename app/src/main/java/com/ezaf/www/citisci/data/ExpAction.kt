package com.ezaf.www.citisci.data

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
    private var samplesCollected = 0
        get() = samplesCollected

    fun incSamples() = { samplesCollected += 1 }

    fun allSamplesWereCollected() = samplesCollected == samplesToCollect

    fun collectData(startTime: Instant) {
        val timePassedFromLatestCapture = Duration.between(lastTimeCollected, Instant.now()).seconds / TIME_DIVISOR

        if(timePassedFromLatestCapture >= captureInterval){
            DataCollecter.collect(sensorType, startTime)
            incSamples()
        }
    }

    override fun toString(): String {
        return  "$captureInterval|$samplesToCollect|$duration|$sensorType"
    }

    fun expDurationHasEnded(startTime: Instant): Boolean {
        val timeNow = Instant.now()
        val diff = Duration.between(startTime, timeNow)
        return (diff.seconds / TIME_DIVISOR) > duration
    }

}
