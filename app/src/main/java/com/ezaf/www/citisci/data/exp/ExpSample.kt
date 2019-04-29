package com.ezaf.www.citisci.data.exp

class ExpSample(val experimentID: String,
                val actionID: String,
                val participantEmail: String = "participant@gmail.com",
                val sample: Any){
    override fun toString(): String {
        return "$experimentID, $actionID, $participantEmail, $sample"
    }
}

class LatLong(val latitude: Double, val longitude: Double)

class MagneticFields(val x: Float, val y: Float, val z: Float)