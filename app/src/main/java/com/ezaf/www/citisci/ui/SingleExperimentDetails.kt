package com.ezaf.www.citisci.ui

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ezaf.www.citisci.R
import kotlinx.android.synthetic.main.single_experiment_details_fragment.*
import android.graphics.Color
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import com.ezaf.www.citisci.data.SensorType
import com.ezaf.www.citisci.data.exp.*
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
            detExp_btnJoinExp.visibility = View.INVISIBLE
        }

        detExp_captureBtn.setOnClickListener { moveToCaptureScreen(it) }
        detExp_tvBtnAbandonExp.setOnClickListener { promptAbandonDialog(it) }
        viewModel = ViewModelProviders.of(this).get(SingleExperimentDetailsViewModel::class.java)
        fillExpDetails()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun fillExpDetails() {
        val participating = isUserParticipatingInThisExp(exp._id)

        if(participating){
            detExp_btnJoinExp.visibility = View.INVISIBLE
            detExp_tvBtnAbandonExp.visibility = View.VISIBLE

            val condCheck: (ExpCondition) -> Boolean = { it.isConditionMet() }
            var allConditionsMet = true
            exp.actions.forEach {
                if(!it.condsList.all(condCheck)){
                    allConditionsMet = false
                }
            }

            if(!allConditionsMet){
                detExp_captureBtn.isClickable = false
                detExp_captureBtn.text = UNAVAILABLE
                detExp_condNotification.visibility = View.VISIBLE
            }
        }


        exp.basicData.run {
            detExp_expName.text = name
            detExp_descriptionText.text = description
            detExp_guideText.text = guide
            detExp_samplesRequiredText.text = getSamplesStatusString()

            setSensorTypeImg(exp.getUniqueParticipatingSensorType())
            setExpTypeImg(automatic)

            if(automatic){
                detExp_captureBtn.visibility = View.INVISIBLE
            }
            else {
                if(!participating){
                    detExp_captureBtn.visibility = View.INVISIBLE
                }
            }

        }

        addActionViews(exp.actions)
    }

    private fun getSamplesStatusString(): String {
        val delim = "/"
        val collected = exp.getSamplesForDisplay().first.toString()
        val total = exp.getSamplesForDisplay().second.toString()
        return if(!SharedDataHelper.fromFeedPageCtx)
            collected + delim + total
        else
            total
    }

    private fun isUserParticipatingInThisExp(id: String): Boolean {
        return viewModel.isUserParticipatingInThisExp(id)
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

    private fun setExpTypeImg(automatic: Boolean) {

        if(automatic) {
            detExp_expType.setImageResource(R.drawable.ic_automatic2)
        }
        else{
            detExp_expType.setImageResource(R.drawable.ic_manual)
        }
    }

    private fun setSensorTypeImg(sensors: MutableSet<SensorType>) {
        var sensorTypeId = 0

        if(sensors.contains(SensorType.MAGNETIC_FIELD))
            sensorTypeId = R.drawable.ic_sensor_magnetic_field
        if(sensors.contains(SensorType.Camera))
            sensorTypeId = R.drawable.ic_sensor_cam
        if(sensors.contains(SensorType.GPS))
            sensorTypeId = R.drawable.ic_sensor_gps

        detExp_sensorType.setImageResource(sensorTypeId)
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

    private fun moveToCaptureScreen(it: View) {
        val type = exp.actions[0].sensorType
        when(type) {
            SensorType.Camera-> startCameraActivity()
            else -> {
                val nextAction = SingleExperimentDetailsDirections.nextAction()
                Navigation.findNavController(it).navigate(nextAction)
            }
        }
    }

    private fun startCameraActivity() {
        activity?.let {
            val intent = Intent (it, CameraActivity::class.java)
            it.startActivity(intent)
        }
    }

    private fun promptAbandonDialog(v: View){
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Warning")
        builder.setMessage(ABANDON_DIALOG_MSG_PROMPT)
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setNegativeButton(ABANDON_DIALOG_MSG_NO) { dialog, which ->
            //do something
        }

        builder.setPositiveButton(ABANDON_DIALOG_MSG_YES) { dialog, which ->
            Toast.makeText(this.context,
                    "Experiment abandoned!", Toast.LENGTH_SHORT).show()
        }

//        builder.setNeutralButton("Maybe") { dialog, which ->
//            Toast.makeText(this.context,
//                    "Maybe", Toast.LENGTH_SHORT).show()
//        }
        builder.show()
    }

}
