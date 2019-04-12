package com.ezaf.www.citisci.utils

import com.ezaf.www.citisci.data.*
import com.ezaf.www.citisci.data.exp.*
import com.ezaf.www.citisci.utils.Logger.log
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
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

    private fun fetchConditions(jsonList: JSONObject): MutableList<String> {
        val list = getObjectsFromJsonArray(jsonList.getJSONArray("conditions"))
        val condStrList : MutableList<String> = mutableListOf()
        for(i in 0 until list.size){
            condStrList.add(list[i].toString().replace("[^0-9+.,]".toRegex(),"").replace(",","$"))
        }
        return condStrList
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

        var baseStr: String
        return when(action.sensorType){
            SensorType.GPS -> {
                if(action.duration != DURATION_IGNORABLE){
                    baseStr = gpsBaseStrAuto
                    baseStr = baseStr.replace(sensor,"GPS coordinate")
                    baseStr = baseStr.replace(time,(action.captureInterval/60).toString())
                    baseStr = baseStr.replace(durationTime, (action.duration).toString())
                }
                else{
                    baseStr = gpsBaseStrManual
                    baseStr = baseStr.replace(samples, action.samplesRequired.toString())
                    baseStr = baseStr.replace(sensor, "GPS coordinates")
                }
                baseStr
            }
            SensorType.Camera -> {
                baseStr = camBaseStr
                baseStr .replace(samples, action.samplesRequired.toString())
            }
            else -> ""
        }
    }
}
