package com.ezaf.www.citisci.data

enum class SensorType {
    GPS,
    Michrophone,
    Camera,
    Unknown
}

fun toSensorType(str:String): SensorType {
    return when(str){
        SensorType.GPS.toString() -> SensorType.GPS
        SensorType.Michrophone.toString() -> SensorType.Michrophone
        SensorType.Camera.toString() -> SensorType.Camera
        else -> SensorType.Unknown
    }
}