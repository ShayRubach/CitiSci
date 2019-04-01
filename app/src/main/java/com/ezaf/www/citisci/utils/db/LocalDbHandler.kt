package com.ezaf.www.citisci.utils.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ezaf.www.citisci.data.exp.ExpAction
import com.ezaf.www.citisci.data.exp.Experiment
import com.ezaf.www.citisci.data.dao.ExpActionDao
import com.ezaf.www.citisci.data.dao.ExperimentDao
import com.ezaf.www.citisci.utils.TypeConverterUtil

const val LOCAL_DB_NAME = "citizen_science_local_room_db"

@Database(entities = [Experiment::class, ExpAction::class], version = 1)
@TypeConverters(TypeConverterUtil::class)
abstract class LocalDbHandler : RoomDatabase() {
    abstract fun experimentDao(): ExperimentDao
    abstract fun expActionsDao(): ExpActionDao

    companion object {
        fun getLocalDb(context: Context): LocalDbHandler {
            return Room.databaseBuilder(context.applicationContext, LocalDbHandler::class.java, LOCAL_DB_NAME).build()
        }
    }


}