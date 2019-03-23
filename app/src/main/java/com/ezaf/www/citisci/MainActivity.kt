package com.ezaf.www.citisci

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ezaf.www.citisci.utils.MainViewModel
import androidx.lifecycle.ViewModelProviders
import com.ezaf.www.citisci.data.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*
import java.time.Instant
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat


class MainActivity : AppCompatActivity() {

    companion object {
        var db: LocalDbHandler? = null
    }
    var expDao: ExperimentDao? = null

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainViewModel = ViewModelProviders.of(this)
                .get(MainViewModel::class.java)

//        DataBindingUtil.setContentView<ActivityMainBinding>(
//                this, R.layout.activity_main
//        ).apply {
//            this.setLifecycleOwner(this@MainActivity)
//            this.viewmodel = mainViewModel
//        }


        if(checkPermissions()){
            requestPermissions()
        }

        startForegroundService(Intent(this, LocationUpdateService::class.java))

        goToCameraBtn.setOnClickListener{
            startActivity(Intent(this, CameraActivity::class.java))
            finish()
        }

        goToGpsActivity.setOnClickListener {
            var interpreter = Interpreter
            interpreter.stopAllScripts()
            startActivity(Intent(this, GpsLocationActivity::class.java))
            finish()
        }

        db = LocalDbHandler.getLocalDb(context = this)
        expDao = db?.experimentDao()
//        testDbInsertionAndSelection()


    }



    fun testDbInsertionAndSelection(){


        var action = ExpAction(2.4,2,100,SensorType.GPS)
        var cond = GpsExpCondition(Pair(100.0,100.0),500.0,"1234",SensorType.GPS)
        var bdata = ExpBasicData("123","name", Instant.now(),"researcher",false,"desc","guide")
        var exp = Experiment(123,ExpScript(expActions = mutableListOf(action),expConditions = mutableListOf(cond)),bdata)

        var action2 = ExpAction(2.4,2,100,SensorType.GPS)
        var cond2 = GpsExpCondition(Pair(100.0,100.0),500.0,"1234",SensorType.GPS)
        var bdata2 = ExpBasicData("345","name", Instant.now(),"researcher",false,"desc","guide")
        var exp2 = Experiment(678,ExpScript(expActions = mutableListOf(action2),expConditions = mutableListOf(cond2)),bdata2)

        Observable.fromCallable {


//            db?.clearAllTables()

            with(expDao){
//                this?.insertExp(exp)
//                this?.insertExp(exp2)

            }
            db?.experimentDao()?.getAllExp()!!
        }.doOnNext {
            for(x:Experiment in it) {
                var re = Regex("\\[|\\]")
                var str = re.replace(x.toString(),"")
//                log("dbg", " \n##################\n$str\n##################\n")
            }
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()

        var interpreter = Interpreter
        interpreter.playScripts(exp2.id.toString())
    }

    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE)

    }
}
