package com.ezaf.www.citisci.utils.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.RecyclerView
import com.ezaf.www.citisci.R
import com.ezaf.www.citisci.data.SensorType
import com.ezaf.www.citisci.data.exp.Experiment
import com.ezaf.www.citisci.ui.FeedPageDirections
import kotlinx.android.synthetic.main.experiment_row.view.*
import java.lang.Integer.min


class FeedPageAdapter(protected val items: List<Experiment>, val context: Context, protected val itemClickListener: (Int, Experiment, NavDirections) -> Unit)
    : RecyclerView.Adapter<FeedPageViewHolder>() {

    // Gets the number of animals in the listOfAllExp
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedPageViewHolder {
        return FeedPageViewHolder(LayoutInflater.from(context).inflate(R.layout.experiment_row, parent, false))
    }

    // Binds each item in the ArrayList to a view
    override fun onBindViewHolder(holder: FeedPageViewHolder, position: Int) {

        val exp: Experiment = items[position]
        holder.bind(exp, itemClickListener)
    }
}

open class FeedPageViewHolder (view: View) : RecyclerView.ViewHolder(view) {

    open val mView = view
    open val mLayout = view.allExp_layout
    open val mName = view.allExp_expName
    open val mResearcher = view.allExp_previewResearcherName
    open val mDescription = view.allExp_previewExpDescription
    open val mProgress = view.allExp_expProgress
    open val mType = view.allExp_expType
    open val mSensors = listOf(
            view.allExp_sensorType1,
            view.allExp_sensorType2,
            view.allExp_sensorType3,
            view.allExp_sensorType4)



    open fun bind(exp: Experiment, itemClickListener: (Int, Experiment, NavDirections) -> Unit) {

        exp.basicData.run {
            mName.text = name
            mResearcher.text = researcher
            mDescription.text = trimAndQuote(description)
            mProgress.text = "999%"
            setExpTypeImageResource(mType, automatic)
        }

        setSensorImageResouce(mSensors, exp.getUniqueParticipatingSensorType())

        mView.setOnClickListener { itemClickListener(adapterPosition, exp, FeedPageDirections.nextAction()) }

    }

    private fun trimAndQuote(description: String): CharSequence? {
        val maxDescriptionChars = 140
        val sb = StringBuilder(description.substring(0, min(maxDescriptionChars, description.length)))
        sb.substring(0, sb.lastIndexOf(" ")+1)
        sb.insert(0,"\"")
        sb.insert(sb.length,"...\"")

        return sb
    }

    protected fun setExpTypeImageResource(mType: ImageView, automatic: Boolean) {
        mType.run {
            setImageResource(if (automatic) R.drawable.ic_automatic2 else R.drawable.ic_manual)
            layoutParams.height = 100 //TODO: fetch real imageview size here
            layoutParams.width = 100
        }
    }

    protected fun setSensorImageResouce(sensors: List<ImageView>, uniqueParticipatingSensorType: MutableSet<SensorType>) {

        //initially - hide all sensors
        sensors.forEach { it.visibility = View.INVISIBLE}

        //set all participating sensors image resources
        sensors.forEach {
            val type: SensorType? = uniqueParticipatingSensorType.elementAtOrNull(0)
            if(type != null){
                it.setImageResource(toSensorIcon(type))
                it.visibility = View.VISIBLE
                it.layoutParams.height = 100 //TODO: fetch real imageview size here
                it.layoutParams.width= 100

                uniqueParticipatingSensorType.remove(type)
            }
        }
    }

    private fun toSensorIcon(type: SensorType) : Int {
        return when(type){
            SensorType.GPS-> R.drawable.ic_sensor_gps
            SensorType.Camera-> R.drawable.ic_sensor_cam
            SensorType.Michrophone-> R.drawable.ic_sensor_mic
            SensorType.MAGNETIC_FIELD-> R.drawable.ic_sensor_magnetic_field
            SensorType.Unknown -> R.drawable.abc_btn_radio_material
            else -> R.drawable.abc_btn_radio_material
        }
    }
}