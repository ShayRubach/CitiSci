package com.ezaf.www.citisci.data.conds

import com.ezaf.www.citisci.data.exp.ExpBasicData.Companion.toInstant
import com.ezaf.www.citisci.data.exp.ExpCondition
import java.time.Instant

class TimeExpCondition (private val startTime: String, private val endTime: String) : ExpCondition {

    override fun isConditionMet(): Boolean {
        val startTimeInstant = toInstant(startTime)
        val endTimeInstant = toInstant(endTime)
        val nowTime = Instant.now()

        return (nowTime.isBefore(startTimeInstant) && nowTime.isAfter(endTimeInstant))
    }

    override fun toString(): String {
        return "start time = $startTime , endTime = $endTime"
    }

}