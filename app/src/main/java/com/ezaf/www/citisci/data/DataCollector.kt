package com.ezaf.www.citisci.data

import org.json.JSONObject
import java.time.Instant

object DataCollector {

    fun collect(sensorType: SensorType, startTime: Instant) : JSONObject? {
        return when(sensorType){
            SensorType.GPS-> collectGpsCoord()
            SensorType.Camera-> null
            SensorType.Michrophone-> null
            SensorType.Unknown -> null
        }
    }

    private fun collectGpsCoord(): JSONObject? {
        TODO("not implemented")
    }
}