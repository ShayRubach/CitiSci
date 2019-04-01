package com.ezaf.www.citisci.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.ezaf.www.citisci.R
import com.ezaf.www.citisci.utils.service.GpsUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_gps_location.*
import java.util.Locale

class GpsLocationActivity: AppCompatActivity() {

    private var mFusedLocationClient:FusedLocationProviderClient? = null
    private var wayLatitude = 0.0
    private var wayLongitude = 0.0
    private var locationRequest:LocationRequest? = null
    private var locationCallback:LocationCallback? = null
    private var stringBuilder:StringBuilder? = null
    private var isContinue = false
    private var isGPS = false

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gps_location)

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
            override fun onLocationResult(locationResult:LocationResult) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    if (location != null) {
                        wayLatitude = location.latitude
                        wayLongitude = location.longitude
                        if (!isContinue) {
                            longtitude_tv.text = String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude)
                        }
                        else {
                            stringBuilder!!.append(wayLatitude)
                            stringBuilder!!.append("-")
                            stringBuilder!!.append(wayLongitude)
                            stringBuilder!!.append("\n\n")
                            latitude_tv.text = stringBuilder.toString()
                        }
                        if (!isContinue && mFusedLocationClient != null) {
                            mFusedLocationClient!!.removeLocationUpdates(locationCallback)
                        }
                    }
                }
            }
        }

        btnLocation.setOnClickListener {
            if (!isGPS) {
                Toast.makeText(this, "Please turn on GPS", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            isContinue = false
            getLocation()
        }

        //TODO: remove dup code
        btnContinueLocation.setOnClickListener {
            //TODO: activate this func once needed. at this point, it just raises complexity of background processing..
            return@setOnClickListener
            if (!isGPS) {
                Toast.makeText(this, "Please turn on GPS", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            isContinue = true
            stringBuilder = StringBuilder()
            getLocation()
        }

    }
    private fun getLocation() {
        if ((ActivityCompat.checkSelfPermission(this@GpsLocationActivity, Manifest.permission.ACCESS_FINE_LOCATION) !== PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this@GpsLocationActivity, Manifest.permission.ACCESS_COARSE_LOCATION) !== PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this@GpsLocationActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    GpsUtils.LOCATION_REQUEST)
        }
        else {
            //TODO: wrap if else in func and remove dup code below
            if (isContinue) {
                mFusedLocationClient!!.requestLocationUpdates(locationRequest, locationCallback, null)
            }
            else {
                mFusedLocationClient!!.lastLocation.addOnSuccessListener(this@GpsLocationActivity) {
                    location->
                    if (location != null) {
                        wayLatitude = location.latitude
                        wayLongitude = location.longitude
                        longtitude_tv.text = String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude)
                    }
                    else {
                        mFusedLocationClient!!.requestLocationUpdates(locationRequest, locationCallback, null)
                    }
                }
            }
        }
    }
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode:Int, @NonNull permissions:Array<String>, @NonNull grantResults:IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            GpsUtils.LOCATION_REQUEST -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO: remove dup code
                    if (isContinue) {
                        mFusedLocationClient!!.requestLocationUpdates(locationRequest, locationCallback, null)
                    }
                    else {
                        mFusedLocationClient!!.lastLocation.addOnSuccessListener(this@GpsLocationActivity) {
                            location->
                            if (location != null) {
                                wayLatitude = location.latitude
                                wayLongitude = location.longitude
                                longtitude_tv.text = String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude)
                            }
                            else {
                                mFusedLocationClient!!.requestLocationUpdates(locationRequest, locationCallback, null)
                            }
                        }
                    }
                }
                else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GpsUtils.GPS_REQUEST) {
                isGPS = true // flag maintain before get location
            }
        }
    }
}