package com.ezaf.www.citisci.utils
import android.util.Log

object Logger {

    val ENABLE_DBG_PRINTS = true
    val GLOBAL_VERBOSE_LVL = VerboseLevel.INFO_ERR    //change this to filter dbg lvls by tags

    fun log(lvl: VerboseLevel = VerboseLevel.INFO_ERR, msg: String) {
        if(ENABLE_DBG_PRINTS && GLOBAL_VERBOSE_LVL >= lvl)
            Log.d(lvl.toString(), msg)
    }
}

enum class VerboseLevel(lvl: Int) {
    LOCATION(1),
    PLACEHOLDER_2(2),
    INFO(3),
    ERR(4),
    INFO_ERR(5)

}