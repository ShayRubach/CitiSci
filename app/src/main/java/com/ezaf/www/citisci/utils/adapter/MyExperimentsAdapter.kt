package com.ezaf.www.citisci.utils.adapter

import android.content.Context
import android.graphics.Color
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.RecyclerView
import com.ezaf.www.citisci.R
import com.ezaf.www.citisci.data.exp.*
import com.ezaf.www.citisci.ui.MyExperimentsDirections
import com.ezaf.www.citisci.utils.Logger
import com.ezaf.www.citisci.utils.VerboseLevel
import kotlinx.android.synthetic.main.my_experiment_row.view.*
import kotlinx.android.synthetic.main.single_experiment_details_fragment.*


class MyExperimentsAdapter(private val items: List<Experiment>, val context: Context, private val itemClickListener: (Int, Experiment, NavDirections) -> Unit)
    : RecyclerView.Adapter<MyExperimentViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyExperimentViewHolder {
        return MyExperimentViewHolder(LayoutInflater.from(context).inflate(R.layout.my_experiment_row, parent, false))
    }

    override fun onBindViewHolder(holder: MyExperimentViewHolder, position: Int) {
        val exp: Experiment = items[position]
        holder.bind(exp, itemClickListener)
    }
}

class MyExperimentViewHolder (view: View) : FeedPageViewHolder(view) {

    private val mSamplesAcquired = view.myExp_samplesAcquired
    private val mProgressRect = view.myExp_progressRect
    private val mNotifImage = view.myExp_notifImage
    override val mView = view
    override val mLayout = view.myExp_layout
    override val mName = view.myExp_expName
    override val mResearcher = view.myExp_previewResearcherName
    override val mProgress = view.myExp_expProgress
    override val mType = view.myExp_expType
    override val mSensors = listOf(
            view.myExp_sensorType1,
            view.myExp_sensorType2,
            view.myExp_sensorType3,
            view.myExp_sensorType4)


    override fun bind(exp: Experiment, itemClickListener: (Int, Experiment, NavDirections) -> Unit) {

        val samplesStatus = exp.getSamplesForDisplay()
        val percentageCompleted = samplesStatus.first.toDouble() / samplesStatus.second.toDouble()

        exp.basicData.run {
            mName.text = name
            mResearcher.text = researcher
            mProgress.text = String.format("%.1f", percentageCompleted * 100)+"%"
            mSamplesAcquired.text = toFixedSamplesAcquiredDisplay(samplesStatus)
            setExpTypeImageResource(mType, automatic)
        }

        mLayout.setBackgroundColor(Color.rgb(255,255,255))
        mNotifImage.visibility = View.INVISIBLE

        val condCheck: (ExpCondition) -> Boolean = { it.isConditionMet() }
        if(!exp.actions[0].condsList.all(condCheck)){
            mLayout.setBackgroundColor(Color.argb(40,255,203,57))
            mNotifImage.setImageResource(R.drawable.ic_warning)
            mNotifImage.visibility = View.VISIBLE
            mProgressRect.visibility = View.INVISIBLE
        }
        else {
            mProgressRect.visibility = View.VISIBLE
            mProgressRect.layoutParams = ConstraintLayout.LayoutParams((SharedDataHelper.screenRes.first * percentageCompleted).toInt(), ConstraintLayout.LayoutParams.WRAP_CONTENT)
        }

        if(exp.actions[0].allSamplesWereCollected()){
            mLayout.setBackgroundColor(Color.argb(85,178,255,204))
            mNotifImage.setImageResource(R.drawable.ic_completed_verbose)
            mProgressRect.visibility = View.INVISIBLE
            mNotifImage.visibility = View.VISIBLE
        }

        setSensorImageResouce(mSensors, exp.getUniqueParticipatingSensorType())
        mView.setOnClickListener { itemClickListener(adapterPosition, exp, MyExperimentsDirections.nextAction()) }
    }

    private fun toFixedSamplesAcquiredDisplay(samplesForDisplay: Pair<Int, Int>): String {
        return SAMPLES_ACQUIRED_PREFIX + samplesForDisplay.first + "/" + samplesForDisplay.second
    }

}