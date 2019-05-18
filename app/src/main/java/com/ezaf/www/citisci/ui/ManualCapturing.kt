package com.ezaf.www.citisci.ui

import android.location.Location
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.ezaf.www.citisci.R
import com.ezaf.www.citisci.data.DataCollector
import com.ezaf.www.citisci.data.SensorType
import com.ezaf.www.citisci.data.exp.*
import com.ezaf.www.citisci.data.exp.SharedDataHelper.magneticFieldValues
import com.ezaf.www.citisci.utils.Logger
import com.ezaf.www.citisci.utils.VerboseLevel
import com.ezaf.www.citisci.utils.db.RemoteDbHandler
import com.ezaf.www.citisci.utils.service.LocationUpdateService
import com.ezaf.www.citisci.utils.viewmodel.ManualCapturingViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.manual_capturing_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class ManualCapturing : Fragment() {

    lateinit var exp: Experiment
    lateinit var sensorType: SensorType
    lateinit var rootView: View
    private val refreshInterval = 1L
    private val tasksManager = CompositeDisposable()
    companion object {
        fun newInstance() = ManualCapturing()
    }

    private lateinit var viewModel: ManualCapturingViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        exp = SharedDataHelper.focusedExp
        sensorType = exp.actions[0].sensorType
        rootView = inflater.inflate(R.layout.manual_capturing_fragment, container, false)
        return rootView
    }

    private fun setPageViewAccordingToSensorType() {
        setSensorImage(sensorType)
        setSensorIdentifierMsg(sensorType)
        tasksManager.add(
                Observable.interval(refreshInterval, TimeUnit.SECONDS)
                        .timeInterval()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe{
                            setSensorCurrentValues(sensorType)
                        }
        )
    }

    private fun setSensorCurrentValues(sensorType: SensorType) {
        tvCurrentSensorValue.run {
            text = when (sensorType){
                SensorType.GPS-> getFormattedLocationStr()
                SensorType.MAGNETIC_FIELD -> getFormattedMagFieldsStr()
                else -> "wtf?"
            }
        }
    }

    private fun getFormattedLocationStr(): String {
        return LocationUpdateService.lastLocationCaptured.latitude.toString() + "\n" +LocationUpdateService.lastLocationCaptured.longitude.toString()
    }

    private fun getFormattedMagFieldsStr(): String {
        return  "X: " + SharedDataHelper.magneticFieldValues[0].toString() + "\n" +
                "Y: " + SharedDataHelper.magneticFieldValues[1].toString() + "\n" +
                "Z: " + SharedDataHelper.magneticFieldValues[2].toString() + "\n"
    }

    private fun setSensorIdentifierMsg(sensorType: SensorType ) {
        tvCurrentSensorIdentifierMsg.run {
            text = when (sensorType){
                SensorType.GPS-> getString(R.string.identifier_msg_gps)
                SensorType.MAGNETIC_FIELD -> getString(R.string.identifier_msg_magnetic)
                else -> "wtf?"
            }
        }

    }

    private fun setSensorImage(sensorType: SensorType ) {
        ivSensorIcon.run {
            //set sensor image
            when (sensorType){
                SensorType.GPS-> setImageResource(R.drawable.ic_sensor_gps)
                SensorType.MAGNETIC_FIELD -> setImageResource(R.drawable.ic_sensor_magnetic_field)
                else -> setImageResource(R.drawable.ic_citisci_logo)
            }
            layoutParams.height = 300 //TODO: fetch real imageview size here
            layoutParams.width = 300

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ManualCapturingViewModel::class.java)
        setPageViewAccordingToSensorType()
        setBtnListeners()
    }

    private fun setBtnListeners() {
        val fn = Throwable().stackTrace[0].methodName

        btnManualCapture.setOnClickListener {
            val msgType = getMsgType()
            val sampleList = prepareSampleList()

            RemoteDbHandler.sendMsg(msgType, sampleList).
                    doOnNext{
                        it.enqueue(object : Callback<ExpSampleList> {
                            override fun onResponse(call: Call<ExpSampleList>, response: Response<ExpSampleList>) {
                                Logger.log(VerboseLevel.INFO, "$fn: sample successfully sent.")
                                Toast.makeText(context, "sample captured!", Toast.LENGTH_SHORT).show()
                                exp.actions[0].updateSamplesStatus()
                            }

                            override fun onFailure(call: Call<ExpSampleList>, t: Throwable) {
                                Logger.log(VerboseLevel.INFO, "$fn: failed to send sample.")
                                Toast.makeText(context, "failed to capture!\nplease try again in a few moments", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                    .subscribeOn(Schedulers.io())
                    .subscribe()
        }
    }

    private fun prepareSampleList(): ExpSampleList {
        val fn = Throwable().stackTrace[0].methodName

        val sampleList = ExpSampleList()
        lateinit var sample : ExpSample
        val thisAction = exp.actions[0]
        when(sensorType){
            SensorType.GPS -> {
                val location = DataCollector.collect(sensorType) as Location
                sample = ExpSample(thisAction.expId, thisAction._id, "participant@gmail.com", LatLong(location.latitude, location.longitude))
            }

            SensorType.MAGNETIC_FIELD -> {
                sample = ExpSample(thisAction.expId, thisAction._id, "participant@gmail.com",
                        MagneticFields(
                                magneticFieldValues[0],
                                magneticFieldValues[1],
                                magneticFieldValues[2])
                )

            }
            else -> Logger.log(VerboseLevel.INFO, "$fn: failed to prepare sample. action=$thisAction.")
        }

        sampleList.addSample(sample)

        return sampleList
    }

    private fun getMsgType(): RemoteDbHandler.MsgType  {
        return when(sensorType){
            SensorType.GPS -> RemoteDbHandler.MsgType.SEND_GPS_SAMPLE
            SensorType.MAGNETIC_FIELD ->RemoteDbHandler.MsgType.SEND_MAGNETIC_FIELD_SAMPLE
            else -> RemoteDbHandler.MsgType.SEND_MIC_SAMPLE
        }
    }

    override fun onDetach() {
        tasksManager.dispose()
        super.onDetach()
    }

}
