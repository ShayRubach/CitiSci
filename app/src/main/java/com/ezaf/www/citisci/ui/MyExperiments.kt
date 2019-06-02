package com.ezaf.www.citisci.ui

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.RecyclerView
import com.ezaf.www.citisci.R
import com.ezaf.www.citisci.data.exp.Experiment
import com.ezaf.www.citisci.data.exp.SharedDataHelper
import com.ezaf.www.citisci.utils.Interpreter
import com.ezaf.www.citisci.utils.Logger
import com.ezaf.www.citisci.utils.ParserUtil
import com.ezaf.www.citisci.utils.VerboseLevel
import com.ezaf.www.citisci.utils.adapter.MyExperimentsAdapter
import com.ezaf.www.citisci.utils.db.RemoteDbHandler
import com.ezaf.www.citisci.utils.viewmodel.MyExperimentsViewModel
import com.google.gson.JsonElement
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.my_experiments_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyExperiments : FeedPage() {

    private lateinit var viewModel: MyExperimentsViewModel
    override lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.my_experiments_fragment, container, false)
        setupRecycler(rootView, R.id.MyExperimentsRecyclerView)
        (activity as AppCompatActivity).supportActionBar?.title = ""
        return rootView
    }

    //TODO: break into small funs and remove code dup
    @SuppressLint("CheckResult")
    override fun setupRecycler(rootView: View, recyclerId: Int) {
        var fn = Throwable().stackTrace[0].methodName

        //context called is MyExperiment, not FeedPage:
        SharedDataHelper.fromFeedPageCtx = false

        if(SharedDataHelper.listOfMyExp.isEmpty()) {
            RemoteDbHandler.getMyExp()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        it.enqueue(object : Callback<JsonElement> {
                            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                                ParserUtil.jsonToExpList(response.body().toString(), SharedDataHelper.listOfMyExp)
                                Logger.log(VerboseLevel.INFO, "$fn:got experiments.")
                                setupRecyclerProperties(rootView, SharedDataHelper.listOfMyExp, recyclerId)

                                Interpreter.playScriptList(SharedDataHelper.listOfMyExp)
                            }

                            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                                Logger.log(VerboseLevel.INFO, "$fn: failed to get experiments.")
                            }
                        })
                    }
        }
        else{
            setupRecyclerProperties(rootView, SharedDataHelper.listOfMyExp, recyclerId)
        }
    }

    override fun attachAdapter(recycler: RecyclerView, expList: List<Experiment>, itemOnClick: (Int, Experiment, NavDirections) -> Unit) {
        recycler.adapter = MyExperimentsAdapter(expList, recycler.context, itemOnClick)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MyExperimentsViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initFilterCheckboxStatus()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initFilterCheckboxStatus() {
        filter_cbRunning.isChecked = true
        filter_cbCompleted.isChecked = true
        filter_cbPaused.isChecked = true
    }

}
