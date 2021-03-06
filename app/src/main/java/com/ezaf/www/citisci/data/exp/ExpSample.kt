package com.ezaf.www.citisci.data.exp

class ExpSample(val experimentID: String,
                val actionID: String,
                val participantEmail: String = SharedDataHelper.currUser,
                val sample: Any){
    override fun toString(): String {
        return "$experimentID, $actionID, $participantEmail, $sample"
    }
}

class LatLong(val latitude: Double, val longitude: Double)

class MagneticFields(val x: Float, val y: Float, val z: Float)

class ImageBase64(val file: String) {
    override fun toString(): String {
        return file
    }
}

