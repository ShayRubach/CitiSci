package com.ezaf.www.citisci.utils
import android.util.Log

object Logger {

    val ENABLE_DBG_PRINTS = true

    fun log(printGroup: String, msg: String) {
        if(ENABLE_DBG_PRINTS)
            Log.d(printGroup,msg)
    }
}