package com.ezaf.www.citisci.data.exp

import com.ezaf.www.citisci.data.SensorType
import com.ezaf.www.citisci.utils.VerboseLevel.*
import com.ezaf.www.citisci.utils.Logger.log
import java.time.Duration
import java.time.Instant

class ExpAction (
        val captureInterval: Double,
        var duration: Int,
        var samplesRequired: Int,
        val _id: String,
        val sensorType: SensorType,
        val condsList : List<ExpCondition>,
        var samplesCollected: Int = 0){

    private val TIME_DIVISOR = 3600.0
    private var lastTimeCollected = Instant.now()
    var expId: String = ""
        private set

    init {
        duration *= 60
        fixSamplesRequiredValueForAutomaticAction()
    }

    private fun fixSamplesRequiredValueForAutomaticAction() {
        if(duration != DURATION_IGNORABLE && captureInterval > 0){
            samplesRequired = (duration / captureInterval*60).toInt()
        }
        log(INFO_ERR, "samplesRequired = $samplesRequired")
    }

    fun updateSamplesStatus() {
        var fn = Throwable().stackTrace[0].methodName
        log(INFO_ERR, "$fn: called.")

        lastTimeCollected = Instant.now()
        samplesCollected++

    }

    fun allSamplesWereCollected() = samplesCollected >= samplesRequired

    fun consumeExpIdOnce(id: String) { expId = id }

    fun isIntervalPassedFromLastCapture()  : Boolean{
        var fn = Throwable().stackTrace[0].methodName
        log(INFO_ERR, "$fn: called.")

        val timePassedFromLatestCapture = Duration.between(lastTimeCollected, Instant.now()).seconds
        log(INFO_ERR, timePassedFromLatestCapture.toString())
        return timePassedFromLatestCapture >= captureInterval
    }

    override fun toString(): String {
        return  "$captureInterval|$duration|$samplesRequired|$_id|$sensorType|$condsList|$samplesCollected"
    }

    fun expDurationHasEnded(startTime: Instant): Boolean {
        return false
        val timeNow = Instant.now()
        val diff = Duration.between(startTime, timeNow)
        return (diff.seconds / TIME_DIVISOR) > duration
    }

}
