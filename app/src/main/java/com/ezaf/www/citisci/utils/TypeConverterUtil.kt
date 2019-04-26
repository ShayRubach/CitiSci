package com.ezaf.www.citisci.utils

import androidx.room.TypeConverter
import com.ezaf.www.citisci.data.*
import com.ezaf.www.citisci.data.exp.ExpAction
import com.ezaf.www.citisci.data.exp.ExpBasicData
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson
import java.util.*


class TypeConverterUtil {

    var gson = Gson()

    @TypeConverter
    fun toListOfString(data: String?): List<String> {
        if (data == null) {
            return Collections.emptyList()
        }

        val listType = object : TypeToken<List<String>>() {

        }.type

        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun fromListOfString(someObjects: List<String>): String {
        return gson.toJson(someObjects)
    }

    @TypeConverter
    fun toListOfExpAction(data: String?): MutableList<ExpAction> {
        if (data == null) {
            return Collections.emptyList()
        }

        val listType = object : TypeToken<MutableList<ExpAction>>() {

        }.type

        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun fromListOfExpAction(someObjects: MutableList<ExpAction>): String {
        return gson.toJson(someObjects)
    }

    @TypeConverter
    fun toInstant(str: String) : Instant {
        return ExpBasicData.toInstant(str)
    }

    @TypeConverter
    fun fromInstant(instant: Instant) : String {
        return DateTimeFormatter.ofPattern(ExpBasicData.TIME_PATTERN).withZone( ZoneId.systemDefault() ).format(instant)
    }

    @TypeConverter
    fun toSensorType(str: String) : SensorType{
        return when(str){
            SensorType.GPS.toString() -> SensorType.GPS
            SensorType.Michrophone.toString() -> SensorType.Michrophone
            SensorType.Camera.toString() -> SensorType.Camera
            else -> SensorType.Unknown
        }
    }

    @TypeConverter
    fun fromSensorType(type: SensorType) : String {
        return type.toString()
    }

    @TypeConverter
    fun toExpAction(str: String) : ExpAction {
        return Gson().fromJson(str, ExpAction::class.java)
    }

    @TypeConverter
    fun fromExpAction(type: ExpAction) : String {
        return Gson().toJson(
                ExpAction(type.captureInterval,
                        type.duration,
                        type.samplesRequired,
                        type._id,
                        type.sensorType,
                        type.condsList,
                        type.samplesCollected))
    }

    @TypeConverter
    fun fromDouble(type: Double) : String {
        return type.toString()
    }

    @TypeConverter
    fun toDouble(type: String) : Double{
        return type.toDouble()
    }
//
}