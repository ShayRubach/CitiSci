package com.ezaf.www.citisci.data.exp

import androidx.room.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ExpBasicData (
        @ColumnInfo(name = "bd_id")
        val _id: String = "DEF_VALUE_ID", //TODO: this id should be the expId. no need to hold additional expId field
        val name: String,
        val startTime: Instant,
        val automatic: Boolean,
        val description: String,
        val guide: String,
        val researcher: String = "researcher name")
{
    @Ignore private var expId: String = ""

    fun consumeExpIdOnce(id: String) { expId = id }

    companion object {
        const val TIME_PATTERN = "yyyy-MM-dd@HH:mm:ss.SSSSSS"

        fun toInstant(str: String) : Instant {
            return LocalDateTime.parse(str, DateTimeFormatter.ofPattern(TIME_PATTERN)).atZone(ZoneId.systemDefault()).toInstant()
        }
    }



    override fun toString(): String {
        return "$_id|$name|"+
                DateTimeFormatter.ofPattern(TIME_PATTERN).withZone( ZoneId.systemDefault() ).format(startTime) +
                "|$automatic|$description|$guide"
    }
}
