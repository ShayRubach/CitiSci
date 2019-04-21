package com.ezaf.www.citisci.data.conds

import android.location.Location
import com.ezaf.www.citisci.data.exp.ExpCondition
import com.ezaf.www.citisci.utils.service.LocationUpdateService
import com.ezaf.www.citisci.utils.Logger.log
import com.ezaf.www.citisci.utils.VerboseLevel

class GpsExpCondition(private val baseCoord: Pair<Double,Double>,
                      private val maxRadius: Double) : ExpCondition
{

    override fun isConditionMet(): Boolean {
        val fn = Throwable().stackTrace[0].methodName

        val distance =  distanceBetweenCoordsInMeters(
                LocationUpdateService.lastLocationCaptured.latitude,
                LocationUpdateService.lastLocationCaptured.longitude,
                baseCoord.first,
                baseCoord.second)

        return distance <= maxRadius
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

    companion object {
        fun toExpConditionList(actionList: List<String>) : List<GpsExpCondition> {
            val fn = Throwable().stackTrace[0].methodName

            val delim = "$"
            var splittedStr: List<String>
            var newList: MutableList<GpsExpCondition> = mutableListOf()

            for(str in actionList){
                splittedStr = str.split(delim)
                newList.add(GpsExpCondition(Pair(splittedStr[0].toDouble(), splittedStr[1].toDouble()), splittedStr[2].toDouble()))
            }
            if(newList.isEmpty()){
                log(VerboseLevel.ERR,"$fn: empty list was generated")
            }
            return newList
        }
    }

    override fun toString(): String {
        return "latitude= ${baseCoord.first}, longitude = ${baseCoord.second}, maxRadios = $maxRadius"
    }
}