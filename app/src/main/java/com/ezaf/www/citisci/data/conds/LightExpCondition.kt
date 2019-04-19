package com.ezaf.www.citisci.data.conds

import com.ezaf.www.citisci.data.SensorType
import com.ezaf.www.citisci.data.exp.ExpCondition
import com.ezaf.www.citisci.data.exp.SharedDataHelper
import com.ezaf.www.citisci.utils.service.LightMode

class LightExpCondition(private val mode: LightMode) : ExpCondition {

    override fun isConditionMet(): Boolean {
        return mode == SharedDataHelper.lightMode
    }

    override fun toString(): String {
        return "mode = ${mode.ordinal+1}"
    }
}