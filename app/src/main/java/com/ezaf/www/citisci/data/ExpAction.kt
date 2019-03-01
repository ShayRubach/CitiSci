package com.ezaf.www.citisci.data

class ExpAction (
        val captureInterval: Double,
        val samplesToCollect: Int,
        val duration: Int,
        val sensorType: SensorType){

    private var samplesCollected = 0
        get() = samplesCollected

    fun incSamples() = { samplesCollected += 1 }

}
