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
import com.ezaf.www.citisci.utils.Logger.log
import com.ezaf.www.citisci.utils.TypeConverterUtil
import com.ezaf.www.citisci.utils.VerboseLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

const val LOCAL_DB_NAME = "citizen_science_local_room_db"

@Database(entities = [Experiment::class], version = 1)
@TypeConverters(TypeConverterUtil::class)
abstract class LocalDbHandler : RoomDatabase() {
    abstract fun experimentDao(): ExperimentDao
    abstract fun expActionsDao(): ExpActionDao

    companion object {
        fun getLocalDb(context: Context): LocalDbHandler {
            return Room.databaseBuilder(context.applicationContext, LocalDbHandler::class.java, LOCAL_DB_NAME).build()
        }
    }


    fun joinExp(exp: Experiment) {
        experimentDao().joinExp(exp._id)
    }

//    fun insertExpList(list: MutableList<Experiment>) = runBlocking {
//        launch(Dispatchers.IO){
//            list.forEach { exp ->
//                exp.actions.forEach { action ->
//                    log(VerboseLevel.INFO_ERR, " \ninserting action = $action\n")
//                    this@LocalDbHandler.expActionsDao().insertAction(action)
//                }
//                log(VerboseLevel.INFO_ERR, " \ninserting exp = $exp\n")
//                this@LocalDbHandler.experimentDao().insertExp(exp)
//            }
//        }
//    }

}