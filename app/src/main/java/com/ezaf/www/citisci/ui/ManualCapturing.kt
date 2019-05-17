package com.ezaf.www.citisci.ui

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ezaf.www.citisci.R

class ManualCapturing : Fragment() {

    companion object {
        fun newInstance() = ManualCapturing()
    }

    private lateinit var viewModel: ManualCapturingViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.manual_capturing_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ManualCapturingViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
