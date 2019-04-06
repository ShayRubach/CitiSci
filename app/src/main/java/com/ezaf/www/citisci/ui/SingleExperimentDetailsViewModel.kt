package com.ezaf.www.citisci.ui

import androidx.lifecycle.ViewModel
import com.ezaf.www.citisci.data.exp.Experiment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SingleExperimentDetailsViewModel : ViewModel() {

    fun joinExp(exp: Experiment, notifyUserWithSuccess: Unit) = runBlocking {

        launch(Dispatchers.IO){
            MainActivity.localDbHandler.joinExp(exp)
            notifyUserWithSuccess
        }
    }
}
