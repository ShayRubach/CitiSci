package com.ezaf.www.citisci.data.exp

import com.ezaf.www.citisci.utils.service.LightMode

object SharedDataHelper {
    lateinit var focusedExp: Experiment
    var listOfAllExp : MutableList<Experiment> = mutableListOf()
    var listOfMyExp : MutableList<Experiment> = mutableListOf()
    lateinit var screenRes: Pair<Int, Int>
    var lightMode = LightMode.BRIGHT
    var magneticFieldValues = floatArrayOf()
}