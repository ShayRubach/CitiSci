package com.ezaf.www.citisci.data

import android.Manifest
import android.content.Intent
import android.os.IBinder
import com.ezaf.www.citisci.utils.Logger
import com.ezaf.www.citisci.utils.VerboseLevel.*
import android.R
import android.app.*
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.ezaf.www.citisci.utils.GpsUtils
import com.ezaf.www.citisci.utils.Logger.log
import com.google.android.gms.location.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class LocationUpdateService : Service() {

    private val LOCATION_SERVICE_OPERATION_INTERVAL = 5L
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var wayLatitude = 0.0
    private var wayLongitude = 0.0
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private var isContinue = false
    private var isGPS = false
    var observablesManager =  CompositeDisposable()

    override fun onCreate() {
        var fn = Throwable().stackTrace[0].methodName
        Logger.log(INFO_ERR, "$fn: called.")
        super.onCreate()

        //run the service
        startMyOwnForeground()


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest!!.interval = 10 * 1000 // 10 seconds
        locationRequest!!.fastestInterval = 5 * 1000 // 5 seconds

        GpsUtils(this).turnGPSOn(object: GpsUtils.onGpsListener {
            override fun gpsStatus(isGPSEnable:Boolean) {
                // turn on GPS
                isGPS = isGPSEnable
            }
        })

        locationCallback = object:LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                //do nothing for now
            }
        }

        observablesManager.add(
                Observable.interval(LOCATION_SERVICE_OPERATION_INTERVAL, TimeUnit.SECONDS)
                        .timeInterval()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe{
                            getLocation()
                        }
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        observablesManager.dispose()
    }

    companion object {
        var lastLocationCaptured = Location("0.0")
    }

    fun getLocation() {
        var fn = "LocationUpdateService::"+Throwable().stackTrace[0].methodName
        Logger.log(INFO_ERR, "$fn: called.")

        if ((ActivityCompat.checkSelfPermission(this@LocationUpdateService, Manifest.permission.ACCESS_FINE_LOCATION) !== PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this@LocationUpdateService, Manifest.permission.ACCESS_COARSE_LOCATION) !== PackageManager.PERMISSION_GRANTED)) {
            //request permission
        }
        else {
            if (isContinue) {
                mFusedLocationClient!!.requestLocationUpdates(locationRequest, locationCallback, null)
            }
            else {
                //TODO: we need to use requestLocationUpdates (GoogleApiClient, LocationRequest, PendingIntent) API instead of this and lose the Observable.interval..
                mFusedLocationClient!!.lastLocation.addOnSuccessListener {
                    location->
                    if (location != null) {
                        wayLatitude = location.latitude
                        wayLongitude = location.longitude
                        lastLocationCaptured.latitude = location.latitude
                        lastLocationCaptured.longitude = location.longitude

                        log(INFO_ERR,"$fn: ${location.latitude} , ${location.longitude}")
                    }
                    else {
                        mFusedLocationClient!!.requestLocationUpdates(locationRequest, locationCallback, null)
                    }
                }
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun startMyOwnForeground() {
        var fn = Throwable().stackTrace[0].methodName
        Logger.log(INFO_ERR, "$fn: called.")

        val NOTIFICATION_CHANNEL_ID = "com.ezaf.www.CitizenScience"
        val channelName = "location update background service"
        val chan = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(chan)

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.arrow_up_float)
                .setContentTitle("$channelName:: App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
        startForeground(2, notification)
    }
}