package com.ezaf.www.citisci.data.conds

import com.ezaf.www.citisci.data.exp.ExpCondition

class TemperatureExpCondition(private val above: Double = 0.0, private val under: Double = 50.0) : ExpCondition {

    override fun isConditionMet(): Boolean {
        return true
    }
}