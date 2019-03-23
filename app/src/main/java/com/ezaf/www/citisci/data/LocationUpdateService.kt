package com.ezaf.www.citisci.data

import android.Manifest
import android.content.Intent
import android.os.IBinder
import com.ezaf.www.citisci.utils.Logger
import com.ezaf.www.citisci.utils.VerboseLevel.*
import android.R
import android.annotation.SuppressLint
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


class LocationUpdateService : Service() {

    private val locationUpdateInterval: Long = 15000       // 15sec
    private val locationUpdateFastInterval: Long = 5000  //  5sec
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private var isGPS = false

    override fun onCreate() {
        var fn = Throwable().stackTrace[0].methodName
        Logger.log(INFO_ERR, "$fn: called.")
        super.onCreate()

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startMyOwnForeground()
        createLocationRequest()
        createLocationCallback()
        requestLocationUpdates()

    }

    private fun createLocationCallback() {
        locationCallback = object:LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                var fn = Throwable().stackTrace[0].methodName
                lastLocationCaptured.latitude = locationResult.lastLocation.latitude
                lastLocationCaptured.longitude = locationResult.lastLocation.longitude
                Logger.log(LOCATION, "$fn: lastLocation=${locationResult.lastLocation.latitude}, ${locationResult.lastLocation.longitude}")
            }
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create()
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest!!.interval = locationUpdateInterval
        locationRequest!!.fastestInterval = locationUpdateFastInterval

        GpsUtils(this).turnGPSOn(object: GpsUtils.onGpsListener {
            override fun gpsStatus(isGPSEnable:Boolean) {
                // turn on GPS
                isGPS = isGPSEnable
            }
        })
    }

    companion object {
        var lastLocationCaptured = Location("0.0")
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        var fn = "LocationUpdateService::"+Throwable().stackTrace[0].methodName
        Logger.log(INFO_ERR, "$fn: called.")

        if(checkPermissions()){
            Logger.log(INFO_ERR, "$fn: we need to request permissions and make sure they are enabled in settings...")
        }
            mFusedLocationClient!!.requestLocationUpdates(locationRequest, locationCallback, null)

    }

    @SuppressLint("MissingPermission")
    private fun requestLastLocation(){
        var fn = "LocationUpdateService::"+Throwable().stackTrace[0].methodName
        Logger.log(INFO_ERR, "$fn: called.")

        if(checkPermissions()){
            mFusedLocationClient!!.lastLocation.addOnSuccessListener {
                location->
                if (location != null) {
                    lastLocationCaptured.latitude = location.latitude
                    lastLocationCaptured.longitude = location.longitude

                    log(INFO_ERR,"$fn: ${location.latitude} , ${location.longitude}")
                }
            }
        }
    }

    fun removeLocationUpdates() {
        var fn = "LocationUpdateService::"+Throwable().stackTrace[0].methodName
        Logger.log(INFO_ERR, "$fn: called.")

        if(checkPermissions()){
            mFusedLocationClient!!.removeLocationUpdates(locationCallback)
        }
    }

    private fun checkPermissions(): Boolean {
        return (ActivityCompat.checkSelfPermission(this@LocationUpdateService, Manifest.permission.ACCESS_FINE_LOCATION) and
                        ActivityCompat.checkSelfPermission(this@LocationUpdateService, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
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