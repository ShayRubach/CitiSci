package com.ezaf.www.citisci.utils

import androidx.room.TypeConverter
import com.ezaf.www.citisci.data.*
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
    fun toExpAction(str: String) : ExpAction{
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
                        type.conditions,
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
//    @TypeConverter
//    fun scriptToStr(script: ExpScript): String {
//        script.run {
//            return "$script"
//        }
//    }
//
//    @TypeConverter
//    fun strToScript(str: String): ExpScript? {
//
////        log(INFO_ERR, "strToScript: str=\n$str")
//        var newStr = str.replace("[","").replace("]","")
//        val seperator = newStr.indexOf("\n")
//        var actionsStr = newStr.substring(0,seperator).split("|")
//        var condsStr = newStr.substring(seperator).split("|")
//
//
//        var newAction = ExpAction(actionsStr[0].toDouble(),actionsStr[1].toInt(),actionsStr[2].toInt(), toSensorType(actionsStr[3]), actionsStr[4].toInt())
//        var newCond = when(newAction.sensorType){
//            //todo: add the reset of the sensors here when implemented
//            SensorType.GPS -> GpsExpCondition(
//                    toDoublePair(condsStr[0]),
//                    condsStr[1].toDouble(),
//                    condsStr[2],
//                    toSensorType(condsStr[3]))
//
//            else -> null
//        }
//        return ExpScript(actions = mutableListOf(newAction), expConditions = mutableListOf(newCond as ExpCondition))
//    }
//
//    @TypeConverter
//    fun basicDataToStr(basicData: ExpBasicData): String {
//        basicData.run {
//            return "$basicData"
//        }
//    }
//
//    @TypeConverter
//    fun strToBasicData(str: String?): ExpBasicData? {
//        str?.run{
//            var strArray = str.split("|")
//            var instant = ExpBasicData.toInstant(strArray[2])
//            return ExpBasicData(strArray[0], strArray[1], instant ,strArray[3], strArray[4].toBoolean(), strArray[5], strArray[6])
//        }
//        return null
//    }
//
//    private fun toDoublePair(str:String): Pair<Double,Double>{
//        return Pair(
//                str.replace(" ","").substring(0,str.indexOf(",")).toDouble(),
//                str.replace(" ","").substring(str.indexOf(",")+1).toDouble()
//        )
//    }

}