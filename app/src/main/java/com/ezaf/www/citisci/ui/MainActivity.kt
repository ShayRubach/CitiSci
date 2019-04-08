package com.ezaf.www.citisci.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.ViewModelProviders
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*
import android.content.pm.PackageManager
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.ezaf.www.citisci.R
import com.ezaf.www.citisci.data.SensorType
import com.ezaf.www.citisci.data.dao.ExpActionDao
import com.ezaf.www.citisci.data.dao.ExperimentDao
import com.ezaf.www.citisci.data.exp.ExpAction
import com.ezaf.www.citisci.data.exp.ExpBasicData
import com.ezaf.www.citisci.data.exp.Experiment
import com.ezaf.www.citisci.utils.Interpreter
import com.ezaf.www.citisci.utils.Logger
import com.ezaf.www.citisci.utils.Logger.log
import com.ezaf.www.citisci.utils.ParserUtil
import com.ezaf.www.citisci.utils.VerboseLevel
import com.ezaf.www.citisci.utils.db.LocalDbHandler
import com.ezaf.www.citisci.utils.service.LocationUpdateService
import com.ezaf.www.citisci.utils.db.RemoteDbHandler
import com.google.gson.JsonElement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Instant
import java.util.*
import kotlin.concurrent.schedule


class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var localDbHandler: LocalDbHandler
        var list : MutableList<Experiment> = mutableListOf()
    }


    lateinit var expDao: ExperimentDao
    lateinit var expActionDao: ExpActionDao


    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        setSupportActionBar(toolbar)

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)


        setupBottomNavMenu(navController)
        setupSideNavigationMenu(navController)
        setupActionBar(navController)



//        goToCameraBtn.setOnClickListener{
//            startActivity(Intent(this, CameraActivity::class.java))
//            finish()
//        }


        if(checkPermissions()){
            requestPermissions()
        }

        //called on the permission result handler
//        startForegroundService(Intent(this, LocationUpdateService::class.java))

        localDbHandler = LocalDbHandler.getLocalDb(context = this)
        expDao = localDbHandler.experimentDao()
        expActionDao = localDbHandler.expActionsDao()
//        testDbInsertionAndSelection()

//        getAllExpFromRemoteDb()


    }

    private fun setupBottomNavMenu(navController: NavController) {
        bottom_nav?.let {
            NavigationUI.setupWithNavController(it, navController)
        }
    }

    private fun setupSideNavigationMenu(navController: NavController) {
        nav_view?.let {
            NavigationUI.setupWithNavController(it, navController)
        }
    }

    private fun setupActionBar(navController: NavController) {
        NavigationUI.setupActionBarWithNavController(this, navController, drawer_layout)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        val navigated = NavigationUI.onNavDestinationSelected(item!!, navController)
        return navigated || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
                Navigation.findNavController(this, R.id.nav_host_fragment),
                drawer_layout)
    }

    private fun getAllExpFromRemoteDb() = runBlocking {


        RemoteDbHandler.getAllExp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    it.enqueue(object : Callback<JsonElement> {
                        override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                            ParserUtil.jsonToExpList(response.body().toString(), list)
                            Logger.log(VerboseLevel.INFO, "got all experiments.")
                        }

                        override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                            Logger.log(VerboseLevel.INFO, "failed to get all experiments.")
                        }
                    })


                }
    }

    fun testDbInsertionAndSelection(){


        val desc1 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
        val desc2 = "Risus in hendrerit gravida rutrum quisque non tellus orci. Laoreet sit amet cursus sit amet dictum. Sapien faucibus et molestie ac feugiat sed lectus vestibulum mattis. Neque vitae tempus quam pellentesque nec nam aliquam sem. Urna nec tincidunt praesent semper feugiat nibh. Blandit cursus risus at ultrices. Sed lectus vestibulum mattis ullamcorper velit. Varius duis at consectetur lorem donec massa sapien faucibus. Viverra vitae congue eu consequat ac felis. Faucibus nisl tincidunt eget nullam non nisi est. Turpis egestas sed tempus urna et pharetra pharetra massa massa. Purus sit amet volutpat consequat. Malesuada proin libero nunc consequat interdum. Sed cras ornare arcu dui. Morbi blandit cursus risus at ultrices. A iaculis at erat pellentesque adipiscing commodo elit. Pulvinar elementum integer enim neque. Risus feugiat in ante metus dictum at tempor. Risus pretium quam vulputate dignissim suspendisse in."

        var bdata = ExpBasicData("1","Morning Glory", Instant.now(),false,desc1,"guide")
        var bdata2 = ExpBasicData("123","How far?", Instant.now(),true,desc2,"gg")

        Observable.fromCallable {
            localDbHandler.clearAllTables()

        }.doOnNext {
//            var action1 = ExpAction(1.0,60,500,"ACTION_1", SensorType.GPS, listOf("32.060709$34.812342$1000.0"))
//            var action2 = ExpAction(2.0,60,10,"ACTION_2", SensorType.Camera, listOf("32.060709$34.812342$1000.0"))
//            var action3 = ExpAction(3.0,10,1,"ACTION_3", SensorType.Michrophone, listOf("32.060709$34.812342$1000.0"))
//            var action4 = ExpAction(2.22,100,32,"ACTION_4", SensorType.Michrophone, listOf("1.3$7.5$23"))
//            var action5 = ExpAction(2.4,30,402,"ACTION_5", SensorType.Camera, listOf("3.3.0$234.5$722"))
//
//            var exp1 = Experiment("23sdf78sd",bdata, mutableListOf(action1._id,action2._id,action3._id))
//            var exp2 = Experiment("9sdf87122",bdata2, mutableListOf(action4._id,action5._id))

//            MainActivity.list.add(exp1)
//            MainActivity.list.add(exp2)


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
