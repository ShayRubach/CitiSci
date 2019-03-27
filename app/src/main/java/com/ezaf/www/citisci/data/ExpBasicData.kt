package com.ezaf.www.citisci.data

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ExpBasicData (
        @ColumnInfo(name = "bd_id")
        val _id: String,
        val name: String,
        val startTime: Instant,
        val automatic: Boolean,
        val description: String,
        val guide: String)
{
    @Ignore private var expId: String = ""

    fun consumeExpIdOnce(id: String) { expId = id }

    companion object {
        const val TIME_PATTERN = "yyyy-MM-dd@HH:mm:ss.SSSSSS"

        fun toInstant(str: String) : Instant {
            return LocalDateTime.parse(str, DateTimeFormatter.ofPattern(ExpBasicData.TIME_PATTERN)).atZone(ZoneId.systemDefault()).toInstant()
        }
    }



    override fun toString(): String {
        return "$_id|$name|"+
                DateTimeFormatter.ofPattern(TIME_PATTERN).withZone( ZoneId.systemDefault() ).format(startTime) +
                "|$automatic|$description|$guide"
    }
}
