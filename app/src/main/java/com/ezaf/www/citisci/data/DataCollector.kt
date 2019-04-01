package com.ezaf.www.citisci.data

import android.location.Location
import com.ezaf.www.citisci.utils.service.LocationUpdateService
import com.ezaf.www.citisci.utils.Logger.log
import com.ezaf.www.citisci.utils.VerboseLevel.*

object DataCollector {

    fun collect(sensorType: SensorType) : Any {
        var fn = Throwable().stackTrace[0].methodName
        log(INFO_ERR, "$fn: called.")

        return when(sensorType){
            SensorType.GPS-> collectGpsCoord()
            SensorType.Camera-> collectGpsCoord()
            SensorType.Michrophone-> collectGpsCoord()
            SensorType.Unknown -> collectGpsCoord()
        }
    }

    private fun collectGpsCoord(): Any {
        //TODO("not implemented")
        var fn = Throwable().stackTrace[0].methodName
        log(INFO_ERR, "$fn: called.")

        //copy the last location captures (avoid race conditions over the value)
        return Location(LocationUpdateService.lastLocationCaptured)
    }
}