package com.ezaf.www.citisci.utils

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    fun onAckTest(){
        Logger.log("testDbg", "ack");
    }

}
