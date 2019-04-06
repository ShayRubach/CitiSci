package com.ezaf.www.citisci.data.dao

import androidx.room.*
import com.ezaf.www.citisci.data.exp.Experiment

const val Q_GET_EXP_BY_ID   = "SELECT * FROM Experiment WHERE _id == :id"
const val Q_GET_ALL_EXP     = "SELECT * FROM Experiment"
const val Q_UPDATE_PARTICIPATING_STATUS = "UPDATE Experiment SET participating = '1' WHERE _id == :id"


@Dao
interface ExperimentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExp(exp: Experiment)

    @Update
    fun updateExp(exp: Experiment)

    @Delete
    fun deleteExp(exp: Experiment)

    @Query(Q_GET_EXP_BY_ID)
    fun getExpById(id: String): Experiment

    @Query(Q_GET_ALL_EXP)
    fun getAllExp(): List<Experiment>

    @Query(Q_UPDATE_PARTICIPATING_STATUS)
    fun joinExp(id: String)

}