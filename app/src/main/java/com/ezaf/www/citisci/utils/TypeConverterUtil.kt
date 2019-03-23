package com.ezaf.www.citisci.utils

import androidx.room.TypeConverter
import com.ezaf.www.citisci.data.*
import com.ezaf.www.citisci.utils.Logger.log
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.ezaf.www.citisci.utils.VerboseLevel.*

class TypeConverterUtil {
    @TypeConverter
    fun scriptToStr(script: ExpScript): String {
        script.run {
            return "$script"
        }
    }

    @TypeConverter
    fun strToScript(str: String): ExpScript? {

//        log(INFO_ERR, "strToScript: str=\n$str")
        var newStr = str.replace("[","").replace("]","")
        val seperator = newStr.indexOf("\n")
        var actionsStr = newStr.substring(0,seperator).split("|")
        var condsStr = newStr.substring(seperator).split("|")


        var newAction = ExpAction(actionsStr[0].toDouble(),actionsStr[1].toInt(),actionsStr[2].toInt(), toSensorType(actionsStr[3]))
        var newCond = when(newAction.sensorType){
            //todo: add the reset of the sensors here when implemented
            SensorType.GPS -> GpsExpCondition(
                    toDoublePair(condsStr[0]),
                    condsStr[1].toDouble(),
                    condsStr[2],
                    toSensorType(condsStr[3]))

            else -> null
        }
        return ExpScript(expActions = mutableListOf(newAction), expConditions = mutableListOf(newCond as ExpCondition))
    }

    @TypeConverter
    fun basicDataToStr(basicData: ExpBasicData): String {
        basicData.run {
            return "$basicData"
        }
    }

    @TypeConverter
    fun strToBasicData(str: String?): ExpBasicData? {
        str?.run{
            var strArray = str.split("|")
            var instant = ExpBasicData.toInstant(strArray[2])
            return ExpBasicData(strArray[0], strArray[1], instant ,strArray[3], strArray[4].toBoolean(), strArray[5], strArray[6])
        }
        return null
    }

    private fun toDoublePair(str:String): Pair<Double,Double>{
        return Pair(
                str.replace(" ","").substring(0,str.indexOf(",")).toDouble(),
                str.replace(" ","").substring(str.indexOf(",")+1).toDouble()
        )
    }

}