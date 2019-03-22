package com.ezaf.www.citisci.data

import android.location.Location
import com.ezaf.www.citisci.utils.Logger.log
import com.ezaf.www.citisci.utils.VerboseLevel

class GpsExpCondition(private val baseCoord: Pair<Double,Double>,
                      private val maxRadius: Double,
                      _id: String,
                      _sensorType: SensorType) : ExpCondition
{
    override val id = _id
    override val sensorType = _sensorType

    override fun isConditionMet(): Boolean {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return distanceBetweenCoordsInMeters(
                LocationUpdateService.lastLocationCaptured.latitude,
                LocationUpdateService.lastLocationCaptured.longitude,
                baseCoord.first,
                baseCoord.second
        ) > maxRadius
    }

    override fun toString(): String {
        return  baseCoord.toString().replace("(","").replace(")","").replace(" ","")+
                "|$maxRadius" +
                "|$id" +
                "|$sensorType"
    }

    private fun distanceBetweenCoordsInMeters(lat1: Double, long1: Double, lat2: Double, long2: Double) : Double
    {
        val loc1 = Location("")
        loc1.latitude = lat1
        loc1.longitude = long1

        val loc2 = Location("")
        loc2.latitude = lat2
        loc2.longitude = long2

        return loc1.distanceTo(loc2).toDouble()
    }
}