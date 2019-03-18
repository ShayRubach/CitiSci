package com.ezaf.www.citisci.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ezaf.www.citisci.utils.TypeConverterUtil

@Entity
class Experiment (
        @PrimaryKey
        val id: Int? = null,
        @TypeConverters(TypeConverterUtil::class)
        val expScript: ExpScript,
        @TypeConverters(TypeConverterUtil::class)
        val expBasicData: ExpBasicData) {

        override fun toString(): String {
                return "$id\n$expScript\n$expBasicData"
        }
}
