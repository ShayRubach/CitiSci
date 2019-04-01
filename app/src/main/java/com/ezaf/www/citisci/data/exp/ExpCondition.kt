package com.ezaf.www.citisci.data.exp

import com.ezaf.www.citisci.data.SensorType

interface ExpCondition {
    val id: String
        get() = id

    val sensorType: SensorType
        get() = sensorType

    fun isConditionMet(): Boolean
}