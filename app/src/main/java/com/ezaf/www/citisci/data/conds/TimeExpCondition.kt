package com.ezaf.www.citisci.data.conds

import com.ezaf.www.citisci.data.exp.ExpBasicData.Companion.toInstant
import com.ezaf.www.citisci.data.exp.ExpCondition
import com.ezaf.www.citisci.utils.Logger
import com.ezaf.www.citisci.utils.Logger.log
import com.ezaf.www.citisci.utils.VerboseLevel
import java.time.Instant
import java.time.LocalTime

class TimeExpCondition (private val after: String, private val before: String) : ExpCondition {

    private val delim = ":"
    private val HOURS_IDX = 0
    private val MINUTES_IDX = 1


    override fun isConditionMet(): Boolean {

        if(after.isNullOrEmpty() || before.isNullOrEmpty()){
            return false
        }

        val afterTime = LocalTime.of(after.substring(0,2).toInt(), after.substring(2).toInt())
        val beforeTime = LocalTime.of(before.substring(0,2).toInt(), before.substring(2).toInt())
        val nowTime = LocalTime.now()

        Logger.log(VerboseLevel.LOCATION, "beforeTime=$beforeTime")
        Logger.log(VerboseLevel.LOCATION, "afterTime=$afterTime")
        Logger.log(VerboseLevel.LOCATION, "nowTime=$nowTime")


        return (nowTime.isBefore(beforeTime) && nowTime.isAfter(afterTime))
    }

    override fun toString(): String {
        return "after = $after , before = $before\n"
    }

}