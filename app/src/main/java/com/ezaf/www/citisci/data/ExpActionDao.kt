package com.ezaf.www.citisci.data

import androidx.room.*

const val Q_GET_ACTION_BY_ID   = "SELECT * FROM ExpAction WHERE _id == :id"
const val Q_GET_ALL_ACTIONS     = "SELECT * FROM ExpAction"


@Dao
interface ExpActionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAction(action: ExpAction)

    @Update
    fun updateAction(action: ExpAction)

    @Delete
    fun deleteAction(action: ExpAction)

    @Query(Q_GET_ACTION_BY_ID)
    fun getActionById(id: String): ExpAction

    @Query(Q_GET_ALL_ACTIONS)
    fun getAllActions(): List<ExpAction>

}