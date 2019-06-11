package com.ezaf.www.citisci.utils

import com.ezaf.www.citisci.data.*
import com.ezaf.www.citisci.data.conds.*
import com.ezaf.www.citisci.data.exp.*
import com.ezaf.www.citisci.utils.service.LightMode
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.StringBuilder
import java.time.Instant

const val EMPTY_JSON = "null"
object ParserUtil {

    //dummy objects to be referenced as type
    val ea = ExpAction(0.0, 0, 0, DUMMMY_ID, SensorType.GPS, listOf(), 0)
    val ebd = ExpBasicData(DUMMMY_ID, "", Instant.now(), false, "", "")
    val exp = Experiment(DUMMMY_ID, ebd, mutableListOf())

    const val sensor = "{ST}"
    const val time = "{TIS}"
    const val durationTime = "{DT}"
    const val samples = "{SM}"

    var gpsBaseStrAuto  = "- Capturing $sensor every $time seconds for $durationTime minutes"
    var gpsBaseStrManual  = "- Capture $samples $sensor at the requested locations in guide"
    var camBaseStr = "- Take $samples pictures of subject requested in guide"
    var magneticBaseStr = "- Measuring $sensor around the device every $time seconds for $durationTime minutes"
    var magneticBaseStrManuel = "- Measure the magnetic field around the device $samples times at the requested locations and time in guide"



    fun jsonToExpList(json: String, expList: MutableList<Experiment>) = runBlocking {

        if(json == EMPTY_JSON){
            Logger.log(VerboseLevel.INFO, "json is empty. doing nothing.\n")
            return@runBlocking
        }

        launch(Dispatchers.IO) {
            val j = JSONObject(json).getJSONArray("message")
            for (i in 0 until j.length()) {
                val expJson = j.getJSONObject(i)

                val actionsListJsonArray = JSONObject(expJson.toString()).getJSONArray("actions")
                val actionList = fetchActions(actionsListJsonArray)
                val actionListIds = getActionsId(actionList)

                val bdataJson = JSONObject(expJson.toString()).get("basicData") as JSONObject
                val bdata = fetchBasicData(bdataJson)


                expJson.run {
                    val exp = Experiment(get(fieldNameAt(exp, 0)).toString(), bdata, actionListIds)
                    exp.attachActions(actionList)
                    expList.add(exp)
                }
            }
            Logger.log(VerboseLevel.INFO, "EXP LIST= \n$expList")
        }
    }

    private fun getActionsId(actionList: MutableList<ExpAction>): MutableList<String> {
        val list = mutableListOf<String>()
        list.run {
            for(a in actionList){
                add(a._id)
            }
        }
        return list
    }

    private fun fetchBasicData(bdataJson: JSONObject): ExpBasicData {

        bdataJson.run {
            return ExpBasicData(
                    "DEFAULT_BD_ID",
                    get(fieldNameAt(ebd, 5)).toString(),
                    Instant.now(),
                    get(fieldNameAt(ebd, 1)).toString().toBoolean(),
                    get(fieldNameAt(ebd, 2)).toString(),
                    get(fieldNameAt(ebd, 4)).toString(),
                    get(fieldNameAt(ebd, 6)).toString()

            )
        }
    }


    private fun fetchActions(jsonList: JSONArray): MutableList<ExpAction> {
        val actionList = mutableListOf<ExpAction>()

        for(i in 0 until jsonList.length()){
            val json = jsonList.getJSONObject(i)
            Logger.log(VerboseLevel.INFO, "json=$json.\n")
            json.run {
                actionList.add(ExpAction(
                        get(fieldNameAt(ea, 2)).toString().toDouble(),
                        get(fieldNameAt(ea, 4)).toString().toInt(),
                        get(fieldNameAt(ea, 8)).toString().toInt(),
                        get(fieldNameAt(ea, 1)).toString(),
                        toSensorType(get(fieldNameAt(ea, 9)).toString()),
                        fetchConditions(jsonList.getJSONObject(i)),
                        try {
                            get(fieldNameAt(ea, 7)).toString().toInt()
                        }catch (e: Exception) {
                            EXCEPTION_IGNORABLE
                        }
                ))
            }
        }
        return actionList
    }

    private fun fetchConditions(jsonList: JSONObject): List<ExpCondition> {
        val list = getObjectsFromJsonArray(jsonList.getJSONArray("conditions"))
        val condsList = mutableListOf<ExpCondition>()

        for(i in 0 until list.size){
            condsList.add(strToCondition(list[i].toString()))
        }
        Logger.log(VerboseLevel.INFO, "condsList=$condsList.\n")
        return condsList
    }

    private fun strToCondition(condStr: String): ExpCondition {
        val strArray = condStr.replace("[^0-9+.,]".toRegex(),"").replace(",","$").split("$")

        condStr.run {
            if(isOfType(SensorType.GPS.toString(),this)){
                return GpsExpCondition(Pair(strArray[1].toDouble(),strArray[2].toDouble()),strArray[3].toDouble())
            }
            if(isOfType(SensorType.Temperature.toString(),this)){
                return TemperatureExpCondition(strArray[1].toDouble(),strArray[2].toDouble())
            }
            if(isOfType(SensorType.Time.toString(),this)){
                return TimeExpCondition(strArray[1].replace(":",""),strArray[2].replace(":",""))
            }
            if(isOfType(SensorType.Light.toString(),this)){
                val mode = if (strArray[1] == LightMode.DARK.ordinal.toString()) LightMode.DARK else LightMode.BRIGHT
                return LightExpCondition(mode)
            }
            if(isOfType(SensorType.MAGNETIC_FIELD.toString(),this)){
                return MagneticFieldExpCondition(
                        strArray[1].toFloat(), strArray[2].toFloat(), strArray[3].toFloat(),
                        strArray[4].toFloat(), strArray[5].toFloat(), strArray[6].toFloat()
                )
            }

        }
        return TimeExpCondition("0","24")

    }

    private fun isOfType(sensorType: String, str: String): Boolean {
        return str.contains(sensorType) || str.contains(sensorType.toUpperCase())
    }


    private fun getObjectsFromJsonArray(array: JSONArray) : MutableList<JSONObject> {
        val list = mutableListOf<JSONObject>()

        for(i in 0 until array.length()){
            list.add(array.getJSONObject(i))
        }
        return list
    }

    private fun fieldNameAt(obj: Any, i: Int): String {
        //used to indicate field number, enable forloop to see indexes.
        val fields = obj::class.java.declaredFields
        /**
        for(f in fields){
            log(VerboseLevel.INFO, "fieldname = ${f.toString().substring(f.toString().lastIndexOf('.')+1)}")
        }*/

        //return exact field by index:
        val field = fields[i].toString()
        return fields[i].toString().substring(field.lastIndexOf('.')+1)
    }

    fun actionParametersToText(action: ExpAction): String {

        val conditionsStr = conditionsParametersToText(action)
        var baseStr: String
        return when(action.sensorType){
            SensorType.GPS -> {
                if(action.duration != DURATION_IGNORABLE){
                    baseStr = gpsBaseStrAuto
                    baseStr = baseStr.replace(sensor,"GPS coordinate")
                    baseStr = baseStr.replace(time,(action.captureInterval).toString())
                    baseStr = baseStr.replace(durationTime, (action.duration).toString())
                }
                else{
                    baseStr = gpsBaseStrManual
                    baseStr = baseStr.replace(samples, action.samplesRequired.toString())
                    baseStr = baseStr.replace(sensor, "GPS coordinates")
                }
                baseStr + conditionsStr
            }
            SensorType.MAGNETIC_FIELD -> {
                if(action.duration != DURATION_IGNORABLE){
                    baseStr = magneticBaseStr
                    baseStr = baseStr.replace(sensor,"magnetic field")
                    baseStr = baseStr.replace(time,(action.captureInterval).toString())
                    baseStr = baseStr.replace(durationTime, (action.duration).toString())
                }
                else{
                    baseStr = magneticBaseStrManuel
                    baseStr = baseStr.replace(samples, action.samplesRequired.toString())
                    baseStr = baseStr.replace(sensor, "magnetic field")
                }
                baseStr + conditionsStr
            }
            SensorType.Camera -> {
                baseStr = camBaseStr
                baseStr .replace(samples, action.samplesRequired.toString()) + conditionsStr
            }
            else -> ""
        }
    }

    private fun conditionsParametersToText(action: ExpAction): Any {
        val sb = StringBuilder("\n\nConditions:\n")
        var num = 1

        action.condsList.forEach {
            sb.append(buildConditionStr(it, num++))
        }

        return sb
    }

    private fun buildConditionStr(cond: ExpCondition, num: Int): String {

        val todBefore = "{BEFORE}"
        val todAfter = "{AFTER}"
        val gpsRadius = "{RD}"
        val gpsBaseCoord = "{BC}"
        val emf = "{EMF}"
        val modeOfLight = "{MOL}"

        val baseCondGpsStr = "$num. Location in radius of $gpsRadius meters from  base point ($gpsBaseCoord) \n"
        val baseCondMagStr = "$num. EMF value (x,y,z) around the device: $emf \n"
        val baseCondTimeStr = "$num. Time of day: $todAfter - $todBefore \n"
        val baseCondLightStr = "$num. Light mode around the device: $modeOfLight \n"


        if(cond is GpsExpCondition){
            val gpsCond = cond as GpsExpCondition
            return baseCondGpsStr.replace(gpsRadius, gpsCond.maxRadius.toString()).replace(gpsBaseCoord, gpsCond.baseCoord.first.toString() +" , "+ gpsCond.baseCoord.second.toString())
        }

        if(cond is LightExpCondition){
            val lightCond = cond as LightExpCondition
            return baseCondLightStr.replace(modeOfLight, if(lightCond.mode.ordinal == 0) "Dark" else "Bright")
        }

        if(cond is MagneticFieldExpCondition){
            val magCond = cond as MagneticFieldExpCondition
            return baseCondMagStr.replace(emf, magCond.toString())
        }

        if(cond is TimeExpCondition){
            val timeCond = cond as TimeExpCondition
            val afterFixedStr = "${timeCond.after.substring(0,2)}:${timeCond.after.substring(2)}"
            val beforeFixedStr = "${timeCond.before.substring(0,2)}:${timeCond.before.substring(2)}"

            return baseCondTimeStr.replace(todAfter, afterFixedStr).replace(todBefore, beforeFixedStr)
        }

        return "NONE"
    }
}
