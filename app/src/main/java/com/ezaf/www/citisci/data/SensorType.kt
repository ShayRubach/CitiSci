package com.ezaf.www.citisci.data

enum class SensorType {
    GPS,
    Michrophone,
    Camera,
    Temperature,
    Light,
    Time,
    Unknown
}

fun toSensorType(str:String): SensorType {
    return when(str){
        SensorType.GPS.toString(), "GPS" -> SensorType.GPS
        SensorType.Michrophone.toString(), "MICROPHONE" -> SensorType.Michrophone
        SensorType.Camera.toString(), "CAMERA" -> SensorType.Camera
        SensorType.Temperature.toString(), "TEMPERATURE" -> SensorType.Temperature
        SensorType.Time.toString(), "TIME" -> SensorType.Time
        SensorType.Light.toString(), "LIGHT" -> SensorType.Light
        else -> SensorType.Unknown
    }
}