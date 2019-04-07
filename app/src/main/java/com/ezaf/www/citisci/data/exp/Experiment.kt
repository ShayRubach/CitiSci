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
        val actions: MutableList<ExpAction> = mutableListOf()
        var participating: Boolean = false


        init {
                publishExpId()
                attachActions()
                calculateSamplesRequired()
                insertToLocalDb()
        }

        private fun calculateSamplesRequired() {
                if(basicData.automatic){
                        actions.forEach {
                                if(it.captureInterval != 0.0){
                                        it.samplesRequired = it.duration/it.captureInterval.toInt()
                                }
                        }
                }
        }

        private fun insertToLocalDb() = runBlocking {
                var fn = Throwable().stackTrace[0].methodName
                log(VerboseLevel.INFO,"$fn: called.\nthis=${this@Experiment}")

                if(_id != DUMMMY_ID){
                        val expDap = localDbHandler.experimentDao()
                        launch(Dispatchers.IO){
                                expDap.insertExp(this@Experiment)
                        }
                }
        }


        private fun attachActions() {
                var fn = Throwable().stackTrace[0].methodName
                log(VerboseLevel.INFO,"$fn: called.\nthis=$this")

                val actionDao = localDbHandler.expActionsDao()
                actions.clear()
                actionDao.run {
                        for(actionID in actionIdList){
                                val action = getActionById(actionID)
//                                log(VerboseLevel.INFO,"ACTION = $action")
                                actions.add(action)
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
