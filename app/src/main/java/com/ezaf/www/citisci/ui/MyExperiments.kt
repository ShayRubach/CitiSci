package com.ezaf.www.citisci.ui

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.RecyclerView
import com.ezaf.www.citisci.R
import com.ezaf.www.citisci.data.exp.Experiment
import com.ezaf.www.citisci.utils.adapter.MyExperimentsAdapter
import com.ezaf.www.citisci.utils.viewmodel.MyExperimentsViewModel


class MyExperiments : FeedPage() {

    private lateinit var viewModel: MyExperimentsViewModel
    override lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.my_experiments_fragment, container, false)
        setupRecycler(rootView, R.id.MyExperimentsRecyclerView)
        return rootView
    }

    override fun attachAdapter(recycler: RecyclerView, expList: List<Experiment>, itemOnClick: (Int, Experiment, NavDirections) -> Unit) {
        recycler.adapter = MyExperimentsAdapter(expList, recycler.context, itemOnClick)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MyExperimentsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
