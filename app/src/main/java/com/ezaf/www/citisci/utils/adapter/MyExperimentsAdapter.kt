package com.ezaf.www.citisci.utils.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ezaf.www.citisci.R
import com.ezaf.www.citisci.data.exp.Experiment
import com.ezaf.www.citisci.data.exp.SAMPLES_ACQUIRED_PREFIX
import com.ezaf.www.citisci.utils.Logger
import com.ezaf.www.citisci.utils.VerboseLevel
import kotlinx.android.synthetic.main.my_experiment_row.view.*

class MyExperimentsAdapter(private val items: List<Experiment>, val context: Context, private val itemClickListener: (Int, Experiment) -> Unit)
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
    override val mView = view
    override val mName = view.myExp_expName
    override val mResearcher = view.myExp_previewResearcherName
    override val mProgress = view.myExp_expProgress
    override val mType = view.myExp_expType
    override val mSensors = listOf(
            view.myExp_sensorType1,
            view.myExp_sensorType2,
            view.myExp_sensorType3,
            view.myExp_sensorType4)


    override fun bind(exp: Experiment, itemClickListener: (Int, Experiment) -> Unit) {

        val samplesStatus = exp.getSamplesForDisplay()
        var percentageCompleted = 0

        if(!exp.basicData.automatic){
            percentageCompleted = samplesStatus.first / samplesStatus.second
        }

        exp.basicData.run {
            mName.text = name
            mResearcher.text = researcher
            mProgress.text = percentageCompleted.toString()+"%"
            mSamplesAcquired.text = toFixedSamplesAcquiredDisplay(samplesStatus)
            setExpTypeImageResource(mType, automatic)
        }

        mProgressRect.width = mView.width * percentageCompleted
        setSensorImageResouce(mSensors, exp.getUniqueParticipatingSensorType())
        mView.setOnClickListener { itemClickListener(adapterPosition, exp) }
    }

    private fun toFixedSamplesAcquiredDisplay(samplesForDisplay: Pair<Int, Int>): String {
        return SAMPLES_ACQUIRED_PREFIX + samplesForDisplay.first + "/" + samplesForDisplay.second
    }

}