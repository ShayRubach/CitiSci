package com.ezaf.www.citisci.data

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ExpBasicData (
        val id: String,
        val name: String,
        val startTime: Instant,
        val researcher: String,
        val automatic: Boolean,
        val desc: String,
        val guide: String)
{

    companion object {
        const val TIME_PATTERN = "yyyy-MM-dd@HH:mm:ss.SSSSSS"

        fun toInstant(str: String) : Instant {
            return LocalDateTime.parse(str, DateTimeFormatter.ofPattern(ExpBasicData.TIME_PATTERN)).atZone(ZoneId.systemDefault()).toInstant()
        }
    }



    override fun toString(): String {
        return "$id|$name|"+
                DateTimeFormatter.ofPattern(TIME_PATTERN).withZone( ZoneId.systemDefault() ).format(startTime) +
                "|$researcher|$automatic|$desc|$guide"
    }
}
