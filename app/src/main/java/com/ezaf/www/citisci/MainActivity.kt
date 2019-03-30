package com.ezaf.www.citisci

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ezaf.www.citisci.utils.MainViewModel
//import androidx.lifecycle.ViewModelProviders
import com.ezaf.www.citisci.data.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*
import java.time.Instant
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ezaf.www.citisci.utils.Logger.log
import com.ezaf.www.citisci.utils.VerboseLevel.*
import com.google.gson.Gson


class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var localDbHandler: LocalDbHandler
    }


    lateinit var expDao: ExperimentDao
    lateinit var expActionDao: ExpActionDao


    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val mainViewModel = ViewModelProviders.of(this)
//                .get(MainViewModel::class.java)

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
//            var interpreter = Interpreter
//            interpreter.stopAllScripts()
            startActivity(Intent(this, GpsLocationActivity::class.java))
            finish()
        }

        localDbHandler = LocalDbHandler.getLocalDb(context = this)
        expDao = localDbHandler.experimentDao()
        expActionDao = localDbHandler.expActionsDao()
        testDbInsertionAndSelection()


    }



    fun testDbInsertionAndSelection(){


        var bdata = ExpBasicData("1","name", Instant.now(),false,"description","guide")
        var bdata2 = ExpBasicData("123","SHAY", Instant.now(),true,"descrddiption","gg")

        var action1 = ExpAction(1.0,60,20,"ACTION_1", SensorType.GPS, listOf("32.060709$34.812342$1000.0"))
        var action2 = ExpAction(2.0,60,10,"ACTION_2", SensorType.GPS, listOf("32.060709$34.812342$1000.0"))
        var action3 = ExpAction(3.0,1,1,"ACTION_3", SensorType.GPS, listOf("32.060709$34.812342$1000.0"))
        var action4 = ExpAction(2.22,1,32,"ACTION_4", SensorType.GPS, listOf("1.3$7.5$23"))
        var action5 = ExpAction(2.4,3,402,"ACTION_5", SensorType.GPS, listOf("3.3.0$234.5$722"))

        var list:List<Experiment> = listOf()

        Observable.fromCallable {
//            localDbHandler.clearAllTables()

            expActionDao.run {
//                insertAction(action1)
//                insertAction(action2)
//                insertAction(action3)
//                insertAction(action4)
//                insertAction(action5)
//                updateAction(action3)
//                var test :List<ExpAction> = getAllActions()
//                for(a in test) {
//                    log(INFO_ERR, " \n##################\n$a\n##################\n")
//                }

//                var test = getActionById("6")
//                log(INFO_ERR, " \n##################\n$test\n##################\n")

            }
//            var exp = Experiment("EXP_1",bdata, mutableListOf(action1._id,action2._id))
//            var exp2 = Experiment("EXP_2",bdata2, mutableListOf(action3._id))


//            log(INFO_ERR, " \n##################\n$exp\n##################")
//            log(INFO_ERR, " \n##################\n$exp2\n##################")

            expDao.run{
//                insertExp(exp)
//                insertExp(exp2)
                getAllExp()
            }


        }.doOnNext{
            for(a in it) {
                log(INFO_ERR, " \n##################\n$a\n##################\n")
            }

        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()

//        var interpreter = Interpreter
//        interpreter.playScripts("EXP_1")
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
