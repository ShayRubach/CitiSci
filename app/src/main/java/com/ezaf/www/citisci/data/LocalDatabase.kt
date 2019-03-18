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
abstract class LocalDatabase : RoomDatabase() {
    abstract fun experimentDao(): ExperimentDao

    companion object {
        var INSTANCE: LocalDatabase? = null

        fun getLocalDb(context: Context): LocalDatabase? {
            if (INSTANCE == null){
                synchronized(LocalDatabase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext, LocalDatabase::class.java, LOCAL_DB_NAME).build()
                }
            }
            return INSTANCE
        }

        fun destroyLocalDb(){
            INSTANCE = null
        }
    }
}