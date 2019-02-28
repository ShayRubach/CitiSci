package com.ezaf.www.citisci.data

class Repository private constructor(private val experimentDao: Experiment) {

    fun joinExp(exp: Experiment){

    }

    fun getExp(id: String){

    }


    companion object {
        @Volatile private var instance : Repository? = null

        fun getInstance(experimentDao: Experiment) =
                instance ?: synchronized(this){
                    instance ?: Repository(experimentDao).also { instance = it }
                }
    }
}