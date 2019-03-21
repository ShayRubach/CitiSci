package com.ezaf.www.citisci.data

import com.ezaf.www.citisci.utils.Logger.log
import com.ezaf.www.citisci.utils.VerboseLevel.INFO_ERR
import org.json.JSONObject
import java.time.Instant

object DataCollector {

    fun collect(sensorType: SensorType, startTime: Instant) : JSONObject? {
        var fn = Throwable().stackTrace[0].methodName
        log(INFO_ERR, "$fn: called.")

        return when(sensorType){
            SensorType.GPS-> collectGpsCoord()
            SensorType.Camera-> null
            SensorType.Michrophone-> null
            SensorType.Unknown -> null
        }
    }

    private fun collectGpsCoord(): JSONObject? {
        TODO("not implemented")
        var fn = Throwable().stackTrace[0].methodName
        log(INFO_ERR, "$fn: called.")
    }
}