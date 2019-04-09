package com.ezaf.www.citisci.data.exp

import androidx.room.*
import com.ezaf.www.citisci.data.SensorType
import com.ezaf.www.citisci.ui.MainActivity.Companion.localDbHandler
import com.ezaf.www.citisci.utils.Logger.log
import com.ezaf.www.citisci.utils.TypeConverterUtil
import com.ezaf.www.citisci.utils.VerboseLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.StringBuilder

@Entity
@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
class Experiment (
        @PrimaryKey
        var _id: String,
        @Embedded
        var basicData: ExpBasicData,
        @TypeConverters(TypeConverterUtil::class)
        var actionIdList: MutableList<String> = mutableListOf()
) {
        @Ignore
        var actions: MutableList<ExpAction> = mutableListOf()
        var participating: Boolean = false


        init {
                publishExpId()
                calculateSamplesRequired()
        }

        private fun calculateSamplesRequired() {
                if(basicData.automatic){
                        actions.forEach {
                                if(it.captureInterval > 0.0 && it.duration > 0.0 ){
                                        it.samplesRequired = it.duration/it.captureInterval.toInt()
                                }
                                else{
                                        log(VerboseLevel.INFO,"samples required aren't defined! it can not be 0!")
                                        it.samplesRequired = 1

                                }
                        }
                }
        }

        private fun publishExpId() {
                basicData.consumeExpIdOnce(_id)
                for(a in actions ){
                        a.consumeExpIdOnce(_id)
                }

        }

        fun getUniqueParticipatingSensorType() : MutableSet<SensorType> {
                val types :MutableSet<SensorType> = mutableSetOf()
                actions.forEach{ types.add(it.sensorType) }
                return types
        }

        override fun toString(): String {
                var builder = StringBuilder("$_id\n$basicData\n$actionIdList\n")
                for(a in actions){
                        builder.append("$a\n")
                }
                builder.append("\n\n")
                return builder.toString()
        }

        fun getSamplesForDisplay() : Pair<Int,Int> {
                var totalSamplesRequired = 0
                var totalSamplesCollected = 0

                actions.forEach {
                        totalSamplesRequired  += it.samplesRequired
                        totalSamplesCollected += it.samplesCollected
                }
                return Pair(totalSamplesCollected,totalSamplesRequired)
        }
}
