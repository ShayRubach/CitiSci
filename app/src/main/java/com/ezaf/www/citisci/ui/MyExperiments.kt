package com.ezaf.www.citisci.ui

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.ezaf.www.citisci.R
import com.ezaf.www.citisci.utils.viewmodel.MyExperimentsViewModel
import kotlinx.android.synthetic.main.my_experiments_fragment.*
import java.util.*


class MyExperiments : Fragment() {

    companion object {
        fun newInstance() = MyExperiments()
    }

    private lateinit var viewModel: MyExperimentsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.my_experiments_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MyExperimentsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
