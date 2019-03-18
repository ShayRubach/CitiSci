package com.ezaf.www.citisci.data

import androidx.room.*

const val Q_GET_EXP_BY_ID   = "SELECT * FROM Experiment WHERE id == :id"
const val Q_GET_ALL_EXP     = "SELECT * FROM Experiment"

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
}