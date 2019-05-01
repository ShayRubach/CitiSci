package com.ezaf.www.citisci.data

import android.location.Location
import com.ezaf.www.citisci.data.exp.SharedDataHelper
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
            SensorType.MAGNETIC_FIELD-> collectMagneticField()
            SensorType.Unknown -> collectGpsCoord()
            else -> collectGpsCoord()

        }
    }

    private fun collectMagneticField(): Any {
        var fn = Throwable().stackTrace[0].methodName
        log(INFO_ERR, "$fn: called.")

        //copy the last magnetic field captured
        return SharedDataHelper.magneticFieldValues
    }

    private fun collectGpsCoord(): Any {
        var fn = Throwable().stackTrace[0].methodName
        log(INFO_ERR, "$fn: called.")

        //copy the last location captured
        return Location(LocationUpdateService.lastLocationCaptured)
    }
}