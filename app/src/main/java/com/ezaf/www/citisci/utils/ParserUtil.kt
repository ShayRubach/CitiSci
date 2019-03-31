package com.ezaf.www.citisci.utils

import com.ezaf.www.citisci.data.*
import com.ezaf.www.citisci.utils.Logger.log
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant

object ParserUtil {


    fun jsonToExpList(json: String) : MutableList<Experiment> {

        val j = JSONObject(json).getJSONArray("message")
        for (i in 0 until j.length()) {
            val expStr = j.getJSONObject(i)
//            Logger.log(VerboseLevel.INFO, "single  json =\n$expStr \n \n \n")

            val bdataJson = JSONObject(expStr.toString()).get("basicData") as JSONObject
            val bdata = fetchBasicData(bdataJson)

            val actionsListJsonArray = JSONObject(expStr.toString()).getJSONArray("actions")
            val actionList = fetchActions(actionsListJsonArray)

        }
        return mutableListOf()
    }

    private fun fetchBasicData(bdataJson: JSONObject): ExpBasicData {
        val type = ExpBasicData("","", Instant.now(),false,"","")

        fieldNameAt(type,0)
        bdataJson.run {
            return ExpBasicData(
//                    get(fieldNameAt(type,0)).toString(),
                    "DEFAULT_BD_ID",
                    get(fieldNameAt(type,5)).toString(),
                    Instant.now(),
                    get(fieldNameAt(type,1)).toString().toBoolean(),
                    get(fieldNameAt(type,2)).toString(),
                    get(fieldNameAt(type,4)).toString()
            )
        }
    }


    private fun fetchActions(jsonList: JSONArray): MutableList<ExpAction> {
        val actionList = mutableListOf<ExpAction>()
        val type = ExpAction(0.0,0,0,"",SensorType.GPS, listOf(),0)

        for(i in 0 until jsonList.length()){
            val json = jsonList.getJSONObject(i)
            json.run {
                actionList.add(ExpAction(
                        get(fieldNameAt(type,2)).toString().toDouble(),
                        get(fieldNameAt(type,4)).toString().toInt(),
                        get(fieldNameAt(type,8)).toString().toInt(),
                        get(fieldNameAt(type,1)).toString(),
                        toSensorType(get(fieldNameAt(type,9)).toString()),
                        fetchConditions(jsonList.getJSONObject(i))
                ))
            }
        }
        log(VerboseLevel.INFO, "list of actions = \n$actionList\n")
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
}