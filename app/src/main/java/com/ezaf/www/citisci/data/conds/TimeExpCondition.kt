package com.ezaf.www.citisci.data.conds

import com.ezaf.www.citisci.data.exp.ExpBasicData.Companion.toInstant
import com.ezaf.www.citisci.data.exp.ExpCondition
import java.time.Instant

class TimeExpCondition (private val after: String, private val before: String) : ExpCondition {

    override fun isConditionMet(): Boolean {

        if(after.isNullOrEmpty() || before.isNullOrEmpty()){
            return false
        }

        val startTimeInstant = toInstant(after)
        val endTimeInstant = toInstant(before)
        val nowTime = Instant.now()

        return (nowTime.isBefore(startTimeInstant) && nowTime.isAfter(endTimeInstant))
    }

    override fun toString(): String {
        return "after = $after , before = $before\n"
    }

}