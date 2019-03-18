package com.ezaf.www.citisci.data

class GpsExpCondition(val baseCoord: Pair<Double,Double>,
                      val maxRadius: Double,
                      _id: String,
                      _sensorType: SensorType) : ExpCondition
{
    override val id = _id
//        get() = super.id

    override val sensorType = _sensorType
//        get() = super.sensorType

    override fun isConditionMet(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toString(): String {
        return  baseCoord.toString().replace("(","").replace(")","").replace(" ","")+
                "|$maxRadius" +
                "|$id" +
                "|$sensorType"
    }
}