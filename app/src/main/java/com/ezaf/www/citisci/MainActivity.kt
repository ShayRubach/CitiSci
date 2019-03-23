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
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


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

        //will be called on the permission result handler
//        startForegroundService(Intent(this, LocationUpdateService::class.java))

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
        return (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED)
    }

    private val MY_PERMISSIONS_REQUEST_FINE_LOCATION = 100

    private fun requestPermissions() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
                        Manifest.permission.READ_CONTACTS)) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION)

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    startForegroundService(Intent(this, LocationUpdateService::class.java))
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }
}
