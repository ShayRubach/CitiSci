package com.ezaf.www.citisci.utils

import android.util.Log
import android.widget.Toast
import androidx.databinding.Bindable
import androidx.lifecycle.ViewModel
import com.ezaf.www.citisci.MainActivity

class MainViewModel : ViewModel() {

    fun onAckTest(){
        Log.d("testDbg", "ack");
    }

}
