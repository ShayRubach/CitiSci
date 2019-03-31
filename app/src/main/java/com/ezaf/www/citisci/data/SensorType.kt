package com.ezaf.www.citisci.data

enum class SensorType {
    GPS,
    Michrophone,
    Camera,
    Unknown
}

fun toSensorType(str:String): SensorType {
    return when(str){
        SensorType.GPS.toString(), "GPS" -> SensorType.GPS
        SensorType.Michrophone.toString(), "MICROPHONE" -> SensorType.Michrophone
        SensorType.Camera.toString(), "CAMERA" -> SensorType.Camera
        else -> SensorType.Unknown
    }
}