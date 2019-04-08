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
import com.ezaf.www.citisci.ui.MainActivity.Companion.localDbHandler
import com.ezaf.www.citisci.utils.Logger
import com.ezaf.www.citisci.utils.VerboseLevel
import com.ezaf.www.citisci.utils.adapter.MyExperimentsAdapter
import com.ezaf.www.citisci.utils.viewmodel.MyExperimentsViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class MyExperiments : FeedPage() {

    companion object {
        fun newInstance() = MyExperiments()
    }

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
        Observable.fromCallable {
            localDbHandler.experimentDao().getMyExp()
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { expList ->

                    Observable.fromCallable {
                        expList .forEach {
                            it.attachActions()
                        }
                    }.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {

                                recyclerView = rootView.findViewById(R.id.MyExperimentsRecyclerView)
                                recyclerView.run {
                                    layoutManager = LinearLayoutManager(context)

                                    //set click listener for item clicked in list
                                    val itemOnClick: (Int, Experiment) -> Unit = { position, exp ->
                                        //                            this.adapter!!.notifyDataSetChanged()
                                        SharedDataHelper.focusedExp = exp
                                        val nextAction = FeedPageDirections.nextAction()
                                        Navigation.findNavController(rootView).navigate(nextAction)
                                        Logger.log(VerboseLevel.INFO, "$fn: called.\n clicked item no. $position , exp=$exp")

                                    }

                                    adapter = MyExperimentsAdapter(expList, context, itemOnClick)
                                    addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
                                    runLayoutAnimation(this)
                            }
                    }
                }
        //TODO: dispose

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MyExperimentsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
