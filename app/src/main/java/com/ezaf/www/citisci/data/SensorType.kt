package com.ezaf.www.citisci.data

enum class SensorType {
    GPS,
    Michrophone,
    Camera,
    Temperature,
    Light,
    Time,
    MAGNETIC_FIELD,
    Unknown
}

fun toSensorType(str:String): SensorType {
    return when(str){
        SensorType.GPS.toString(),
        SensorType.GPS.toString().toUpperCase() -> SensorType.GPS

        SensorType.Michrophone.toString(),
        SensorType.Michrophone.toString().toUpperCase() -> SensorType.Michrophone

        SensorType.Camera.toString(),
        SensorType.Camera.toString().toUpperCase() -> SensorType.Camera

        SensorType.Temperature.toString(),
        SensorType.Temperature.toString().toUpperCase() -> SensorType.Temperature

        SensorType.Time.toString(),
        SensorType.Time.toString().toUpperCase() -> SensorType.Time

        SensorType.Light.toString(),
        SensorType.Light.toString().toUpperCase() -> SensorType.Light

        SensorType.MAGNETIC_FIELD.toString(),
        SensorType.MAGNETIC_FIELD.toString().toUpperCase() -> SensorType.MAGNETIC_FIELD

        else -> SensorType.Unknown
    }
}