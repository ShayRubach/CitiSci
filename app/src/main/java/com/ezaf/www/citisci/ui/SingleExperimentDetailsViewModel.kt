package com.ezaf.www.citisci.ui

import androidx.lifecycle.ViewModel
import com.ezaf.www.citisci.data.exp.ExpAction
import com.ezaf.www.citisci.data.exp.Experiment
import com.ezaf.www.citisci.utils.Interpreter
import com.ezaf.www.citisci.utils.ParserUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SingleExperimentDetailsViewModel : ViewModel() {

    fun joinExp(exp: Experiment, notifyUserWithSuccess: Unit) = runBlocking {

        launch(Dispatchers.IO){
            MainActivity.localDbHandler.joinExp(exp)
            notifyUserWithSuccess
//            val inter = Interpreter
//            inter.playScripts(exp._id)
        }
    }

    fun actionParametersToText(action: ExpAction) : String {
        return ParserUtil.actionParametersToText(action)
    }

}
