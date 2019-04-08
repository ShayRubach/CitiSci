package com.ezaf.www.citisci.data.exp

import androidx.room.*
import com.ezaf.www.citisci.data.SensorType
import com.ezaf.www.citisci.ui.MainActivity.Companion.localDbHandler
import com.ezaf.www.citisci.utils.VerboseLevel.*
import com.ezaf.www.citisci.utils.Logger.log
import com.ezaf.www.citisci.utils.TypeConverterUtil
import com.ezaf.www.citisci.utils.VerboseLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.time.Instant

@Entity
class ExpAction (
        @TypeConverters(TypeConverterUtil::class)
        val captureInterval: Double,
        val duration: Int,
        var samplesRequired: Int,
        @PrimaryKey
        @ColumnInfo(name = "_id")
        val _id: String,
        @TypeConverters(TypeConverterUtil::class)
        val sensorType: SensorType,
        @TypeConverters(TypeConverterUtil::class)
        var conditions: List<String>,
        var samplesCollected: Int = 0){


    @Ignore private val TIME_DIVISOR = 3600.0
    @Ignore private var lastTimeCollected = Instant.now()
    @Ignore var expId: String = ""
        private set

    fun updateSamplesStatus() = runBlocking {
        var fn = Throwable().stackTrace[0].methodName
        log(INFO_ERR, "$fn: called.")

        lastTimeCollected = Instant.now()
        samplesCollected++

        launch(Dispatchers.IO){(localDbHandler.expActionsDao().updateAction(this@ExpAction))}
    }

    fun allSamplesWereCollected() = samplesCollected == samplesRequired

    fun consumeExpIdOnce(id: String) { expId = id }

    fun isIntervalPassedFromLastCapture()  : Boolean{
        var fn = Throwable().stackTrace[0].methodName
        log(INFO_ERR, "$fn: called.")

        val timePassedFromLatestCapture = Duration.between(lastTimeCollected, Instant.now()).seconds
        log(INFO_ERR, timePassedFromLatestCapture.toString())
        return timePassedFromLatestCapture >= captureInterval
    }

    override fun toString(): String {
        return  "$captureInterval|$duration|$samplesRequired|$_id|$sensorType|$conditions|$samplesCollected"
    }

    fun expDurationHasEnded(startTime: Instant): Boolean {

        val timeNow = Instant.now()
        val diff = Duration.between(startTime, timeNow)
        return (diff.seconds / TIME_DIVISOR) > duration
    }

}
