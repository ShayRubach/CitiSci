package com.ezaf.www.citisci.data

import java.time.Instant
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
    override fun toString(): String {
        return "$id|$name|"+
                DateTimeFormatter.ofPattern("yyyy-MM-dd@HH:mm:ss.SSSSSS").withZone( ZoneId.systemDefault() ).format(startTime) +
                "|$researcher|$automatic|$desc|$guide"
    }
}