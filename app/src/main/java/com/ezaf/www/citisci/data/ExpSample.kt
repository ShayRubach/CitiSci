package com.ezaf.www.citisci.data

abstract class ExpSample(var sensorType: SensorType)

class ExpGpsSample(val data: LatLong) : ExpSample(SensorType.GPS)
class ExpCameraSample(val data: String) : ExpSample(SensorType.Camera)
class ExpMicSample(val data: String) : ExpSample(SensorType.Michrophone)

class LatLong(val latitude: String, val longitude: String){

    constructor(lat: Double, lon: Double) : this(lat.toString(), lon.toString())
}