package com.ezaf.www.citisci.utils

import com.ezaf.www.citisci.data.Experiment
import com.ezaf.www.citisci.utils.Logger.log
import org.json.JSONArray
import org.json.JSONObject
import java.lang.StringBuilder

object ParserUtil {


    fun jsonToExpList(json: String) : MutableList<Experiment> {

        val j = JSONObject(json).getJSONArray("message")
        for (i in 0 until j.length()) {
            val expStr = j.getJSONObject(i)
//            Logger.log(VerboseLevel.INFO, "single  json =\n$expStr \n \n \n")

            val bdataJson = JSONObject(expStr.toString()).get("basicData")
//            Logger.log(VerboseLevel.INFO, "bdata   json =\n${JSONObject(expStr.toString()).get("basicData")} \n \n \n")

            val actionsListJsonArray = JSONObject(expStr.toString()).getJSONArray("actions")
//            Logger.log(VerboseLevel.INFO, "AJ = ${JSONObject(expStr.toString()).getJSONArray("actions")} \n \n")

            val actionList = fetchActions(actionsListJsonArray)

        }
        return mutableListOf()
    }


    private fun fetchActions(jsonList: JSONArray): MutableList<JSONObject> {
        var list = getObjectsFromJsonArray(jsonList)
        log(VerboseLevel.INFO, "LI = $list  \n \n")
        for(i in 0 until jsonList.length()){
            fetchConditions(jsonList.getJSONObject(i))
//            list.add(Gson().fromJson(jsonList.getJSONObject(i).toString(),ExpAction::class.java))
            log(VerboseLevel.INFO, "list of actions = \n \n${jsonList.getJSONObject(i)}")
        }
        return list
    }

    private fun fetchConditions(jsonList: JSONObject): MutableList<String> {
        var list = getObjectsFromJsonArray(jsonList.getJSONArray("conditions"))
        var condStrList : MutableList<String> = mutableListOf()
        //answer.replace("[^0-9+.,]".toRegex(),"").replace(".","$")
        for(i in 0 until list.size){
            condStrList.add(list[i].toString().replace("[^0-9+.,]".toRegex(),"").replace(",","$"))
//            log(VerboseLevel.INFO, "XXX = \n \n${list[i]}")
        }
        log(VerboseLevel.INFO, "condStrList = $condStrList")
        return condStrList
    }

//    private fun fetchConditions(array: JSONArray): MutableList<String> {
//        var list = mutableListOf<String>()
//    }

    private fun getObjectsFromJsonArray(array: JSONArray) : MutableList<JSONObject> {
        var list = mutableListOf<JSONObject>()

        for(i in 0 until array.length()){
            list.add(array.getJSONObject(i))
            log(VerboseLevel.INFO, "object in list = \n \n${array.getJSONObject(i)}")
        }
        return list
    }
}