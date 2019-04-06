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
import com.ezaf.www.citisci.utils.Logger
import com.ezaf.www.citisci.utils.VerboseLevel
import kotlinx.android.synthetic.main.single_experiment_details_fragment.*
import android.graphics.Color
import com.ezaf.www.citisci.data.SensorType


class SingleExperimentDetails : Fragment() {

    lateinit var exp: Experiment

    companion object {
        fun newInstance() = SingleExperimentDetails()
    }

    private lateinit var viewModel: SingleExperimentDetailsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var fn = Throwable().stackTrace[0].methodName
        exp = SharedDataHelper.focusedExp
        Logger.log(VerboseLevel.INFO, "$fn: called.\nexp=$exp")

        return inflater.inflate(R.layout.single_experiment_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
        if(!sensors.contains(SensorType.Michrophone))
            detExp_sensorMic.setColorFilter(greyColor)
        if(!sensors.contains(SensorType.Camera))
            detExp_sensorCam.setColorFilter(greyColor)
        if(!sensors.contains(SensorType.GPS))
            detExp_sensorGps.setColorFilter(greyColor)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SingleExperimentDetailsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
