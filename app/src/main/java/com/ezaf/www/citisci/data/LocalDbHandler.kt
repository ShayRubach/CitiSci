package com.ezaf.www.citisci.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ezaf.www.citisci.utils.TypeConverterUtil

const val LOCAL_DB_NAME = "citizen_science_local_room_db"

@Database(entities = [Experiment::class], version = 1)
@TypeConverters(TypeConverterUtil::class)
abstract class LocalDbHandler : RoomDatabase() {
    abstract fun experimentDao(): ExperimentDao

    companion object {
        var INSTANCE: LocalDbHandler? = null

        fun getLocalDb(context: Context): LocalDbHandler? {
            if (INSTANCE == null){
                synchronized(LocalDbHandler::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext, LocalDbHandler::class.java, LOCAL_DB_NAME).build()
                }
            }
            return INSTANCE
        }

        fun destroyLocalDb(){
            INSTANCE = null
        }
    }
}