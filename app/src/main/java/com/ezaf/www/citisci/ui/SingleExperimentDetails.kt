package com.ezaf.www.citisci.ui

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ezaf.www.citisci.R
import com.ezaf.www.citisci.data.exp.Experiment
import com.ezaf.www.citisci.data.exp.SharedDataHelper
import kotlinx.android.synthetic.main.single_experiment_details_fragment.*
import android.graphics.Color
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.ezaf.www.citisci.data.SensorType
import com.ezaf.www.citisci.data.exp.ExpAction
import com.ezaf.www.citisci.utils.viewmodel.SingleExperimentDetailsViewModel


class SingleExperimentDetails : Fragment() {

    lateinit var exp: Experiment

    private lateinit var viewModel: SingleExperimentDetailsViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var fn = Throwable().stackTrace[0].methodName
        exp = SharedDataHelper.focusedExp
        return inflater.inflate(R.layout.single_experiment_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        detExp_btnJoinExp.setOnClickListener {
            viewModel.joinExp(exp, notifyUserWithSuccessJoin(exp.basicData.name))
        }
        viewModel = ViewModelProviders.of(this).get(SingleExperimentDetailsViewModel::class.java)
        fillExpDetails()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun fillExpDetails() {
        val greyColor= (Color.argb(220, 200, 200, 200))

        exp.basicData.run {
            detExp_expName.text = name
            detExp_descriptionText.text = description
            detExp_guideText.text = guide

            greyOutSensors(greyColor, exp.getUniqueParticipatingSensorType())
            greyOutExpType(greyColor, automatic)

            if(automatic){
                detExp_samplesRequiredText.visibility = View.INVISIBLE
                detExp_samplesRequiredTitle.visibility = View.INVISIBLE
            }
        }

        addActionViews(exp.actions)
    }

    private fun addActionViews(actions: MutableList<ExpAction>) {
        actions.forEach {

            val actionViewRow = TextView(this@SingleExperimentDetails.context)
            actionViewRow.run {
                textSize = 20f
                text = viewModel.actionParametersToText(it)
                setPadding(50,15,25,20)
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
            detExp_actionsLayout.addView(actionViewRow)

        }
    }

    private fun greyOutExpType(greyColor: Int, automatic: Boolean) {

        if(automatic) {
            detExp_manual.setColorFilter(greyColor)
        }
        else{
            detExp_automatic.setColorFilter(greyColor)
        }
    }

    private fun greyOutSensors(greyColor: Int, sensors: MutableSet<SensorType>) {
        if(!sensors.contains(SensorType.MAGNETIC_FIELD))
            detExp_sensorMagneticField.setColorFilter(greyColor)
        if(!sensors.contains(SensorType.Camera))
            detExp_sensorCam.setColorFilter(greyColor)
        if(!sensors.contains(SensorType.GPS))
            detExp_sensorGps.setColorFilter(greyColor)
    }

    private val notifyUserWithSuccessJoin: (String) -> Unit  = {
        Toast.makeText(context, "Successfully joined $it", Toast.LENGTH_LONG).show()
        //TODO: move to my exp with this exp focused
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SingleExperimentDetailsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
