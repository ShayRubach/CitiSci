package com.ezaf.www.citisci.utils.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import com.ezaf.www.citisci.R
import com.ezaf.www.citisci.data.DataCollector
import com.ezaf.www.citisci.data.SensorType
import com.ezaf.www.citisci.data.exp.*
import com.ezaf.www.citisci.utils.Logger
import com.ezaf.www.citisci.utils.VerboseLevel
import com.ezaf.www.citisci.utils.db.RemoteDbHandler
import com.ezaf.www.citisci.utils.service.LocationUpdateService

class ManualCapturingViewModel : ViewModel() {

    fun getSensorImage(sensorType: SensorType): Int {
        return when (sensorType){
            SensorType.GPS-> R.drawable.ic_sensor_gps
            SensorType.MAGNETIC_FIELD -> R.drawable.ic_sensor_magnetic_field
            else -> R.drawable.ic_citisci_logo
        }
    }

    fun getSensorIdentifierMsgId(sensorType: SensorType): Int {
        return when (sensorType){
            SensorType.GPS-> R.string.identifier_msg_gps
            SensorType.MAGNETIC_FIELD -> R.string.identifier_msg_magnetic
            else -> R.string.error

        }
    }

    fun getMsgType(sensorType: SensorType): RemoteDbHandler.MsgType {
        return when(sensorType){
            SensorType.GPS -> RemoteDbHandler.MsgType.SEND_GPS_SAMPLE
            SensorType.MAGNETIC_FIELD ->RemoteDbHandler.MsgType.SEND_MAGNETIC_FIELD_SAMPLE
            else -> RemoteDbHandler.MsgType.SEND_MIC_SAMPLE
        }
    }

    fun prepareSampleList(exp: Experiment): ExpSampleList {
        val fn = Throwable().stackTrace[0].methodName
        val thisAction = exp.actions[0]
        val sensorType = thisAction.sensorType
        lateinit var sample : ExpSample
        val sampleList = ExpSampleList()

        when(sensorType){
            SensorType.GPS -> {
                val location = DataCollector.collect(sensorType) as Location
                sample = ExpSample(thisAction.expId, thisAction._id, SharedDataHelper.currUser, LatLong(location.latitude, location.longitude))
            }

            SensorType.MAGNETIC_FIELD -> {
                sample = ExpSample(thisAction.expId, thisAction._id, SharedDataHelper.currUser,
                        MagneticFields(
                                SharedDataHelper.magneticFieldValues[0],
                                SharedDataHelper.magneticFieldValues[1],
                                SharedDataHelper.magneticFieldValues[2])
                )

            }
            else -> Logger.log(VerboseLevel.INFO, "$fn: failed to prepare sample. action=$thisAction.")
        }

        sampleList.addSample(sample)

        return sampleList
    }

    fun getFormattedLocationStr(): String {
        return LocationUpdateService.lastLocationCaptured.latitude.toString() + "\n" + LocationUpdateService.lastLocationCaptured.longitude.toString()
    }

    fun getFormattedMagFieldsStr(): String {
        return  "X: " + SharedDataHelper.magneticFieldValues[0].toString() + "\n" +
                "Y: " + SharedDataHelper.magneticFieldValues[1].toString() + "\n" +
                "Z: " + SharedDataHelper.magneticFieldValues[2].toString() + "\n"
    }

}
