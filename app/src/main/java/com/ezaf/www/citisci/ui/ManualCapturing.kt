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
        tvCurrentSensorValue.text = when (sensorType){
                SensorType.GPS-> viewModel.getFormattedLocationStr()
                SensorType.MAGNETIC_FIELD -> viewModel.getFormattedMagFieldsStr()
                else -> "wtf?"
        }
    }

    private fun setSensorIdentifierMsg(sensorType: SensorType ) {
        tvCurrentSensorIdentifierMsg.text = getString(viewModel.getSensorIdentifierMsgId(sensorType))
    }

    private fun setSensorImage(sensorType: SensorType ) {
        ivSensorIcon.run {
            setImageResource(viewModel.getSensorImage(sensorType))
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
            val msgType = viewModel.getMsgType(sensorType)
            val sampleList = viewModel.prepareSampleList(exp)

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

    override fun onDetach() {
        tasksManager.dispose()
        super.onDetach()
    }

}
