package com.ezaf.www.citisci.ui

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ezaf.www.citisci.R
import com.ezaf.www.citisci.data.exp.Experiment
import com.ezaf.www.citisci.data.exp.SharedDataHelper
import com.ezaf.www.citisci.utils.Logger
import com.ezaf.www.citisci.utils.ParserUtil
import com.ezaf.www.citisci.utils.VerboseLevel
import com.ezaf.www.citisci.utils.adapter.FeedPageAdapter
import com.ezaf.www.citisci.utils.adapter.MyExperimentsAdapter
import com.ezaf.www.citisci.utils.db.RemoteDbHandler
import com.ezaf.www.citisci.utils.viewmodel.MyExperimentsViewModel
import com.google.gson.JsonElement
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyExperiments : FeedPage() {

    private lateinit var viewModel: MyExperimentsViewModel
    override lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.my_experiments_fragment, container, false)
        setupRecycler(rootView)
        return rootView
    }

    @SuppressLint("CheckResult")
    override fun setupRecycler(rootView: View) {
        var fn = Throwable().stackTrace[0].methodName


        if(SharedDataHelper.list.isEmpty()) {
            RemoteDbHandler.getAllExp()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        it.enqueue(object : Callback<JsonElement> {
                            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                                ParserUtil.jsonToExpList(response.body().toString(), SharedDataHelper.list)
                                Logger.log(VerboseLevel.INFO, "got all experiments.")

                                setupRecyclerProperties(rootView, SharedDataHelper.list)
                            }

                            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                                Logger.log(VerboseLevel.INFO, "failed to get all experiments.")
                            }
                        })
                    }
        }
        else{
            setupRecyclerProperties(rootView, SharedDataHelper.list)
        }
    }

    private fun setupRecyclerProperties(rootView: View, expList: List<Experiment>){
        var fn = Throwable().stackTrace[0].methodName

        recyclerView = rootView.findViewById(R.id.MyExperimentsRecyclerView)
        recyclerView.run {
            layoutManager = LinearLayoutManager(context)

            //set click listener for item clicked in list
            val itemOnClick: (Int, Experiment) -> Unit = { position, exp ->
                //                            this.adapter!!.notifyDataSetChanged()
                SharedDataHelper.focusedExp = exp
                val nextAction = MyExperimentsDirections.nextAction()
                Navigation.findNavController(rootView).navigate(nextAction)
                Logger.log(VerboseLevel.INFO, "$fn: called.\n clicked item no. $position , exp=$exp")

            }

            adapter = MyExperimentsAdapter(expList, context, itemOnClick)
            addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
            runLayoutAnimation(this)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MyExperimentsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
