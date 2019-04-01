package com.ezaf.www.citisci.utils.db

import com.ezaf.www.citisci.data.exp.Experiment

class Repository private constructor(private val experimentDao: Experiment) {

    fun joinExp(exp: Experiment){

    }

    fun getExp(id: String){

    }


    companion object {
        @Volatile private var instance : Repository? = null

        fun getInstance(experimentDao: Experiment) =
                instance ?: synchronized(this){
                    instance
                            ?: Repository(experimentDao).also { instance = it }
                }
    }
}