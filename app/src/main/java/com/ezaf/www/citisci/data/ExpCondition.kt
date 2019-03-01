package com.ezaf.www.citisci.data

interface ExpCondition {
    val id: String
        get() = id

    val sensorType: SensorType
        get() = sensorType

    fun isConditionMet(): Boolean
}