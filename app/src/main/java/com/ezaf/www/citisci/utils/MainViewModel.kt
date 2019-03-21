package com.ezaf.www.citisci.utils

import androidx.lifecycle.ViewModel
import com.ezaf.www.citisci.utils.VerboseLevel.INFO_ERR

class MainViewModel : ViewModel() {

    fun onAckTest(){
        Logger.log(INFO_ERR, "ack")

    }

}
