package com.ezaf.www.citisci.utils.viewmodel

import androidx.lifecycle.ViewModel
import com.ezaf.www.citisci.data.exp.ExpAction
import com.ezaf.www.citisci.data.exp.Experiment
import com.ezaf.www.citisci.utils.ParserUtil
import com.ezaf.www.citisci.utils.db.JoinExpRequest
import com.ezaf.www.citisci.utils.db.RemoteDbHandler
import com.google.gson.JsonElement
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SingleExperimentDetailsViewModel : ViewModel() {

    fun joinExp(exp: Experiment, notifyUserWithSuccessJoin: Unit) = runBlocking {

        RemoteDbHandler.joinExp(exp._id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe {
                    it.enqueue(object : Callback<JoinExpRequest> {
                        override fun onResponse(call: Call<JoinExpRequest>, response: Response<JoinExpRequest>) {
                            notifyUserWithSuccessJoin
                        }

                        override fun onFailure(call: Call<JoinExpRequest>, t: Throwable) {

                        }
                    })
                }
    }

    fun actionParametersToText(action: ExpAction) : String {
        return ParserUtil.actionParametersToText(action)
    }

}