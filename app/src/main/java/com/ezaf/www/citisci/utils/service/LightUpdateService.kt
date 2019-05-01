package com.ezaf.www.citisci.utils.service

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.ezaf.www.citisci.data.exp.SharedDataHelper
import com.ezaf.www.citisci.utils.Logger
import com.ezaf.www.citisci.utils.VerboseLevel

enum class LightMode{
    DARK,
    BRIGHT,
    SHADY
}

class LightUpdateService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var light: Sensor? = null

    override fun onCreate() {
        // Get an instance of the sensor service, and use that to get an instance of
        // a particular sensor.
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL)
        startMyOwnForeground()

    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
    }

    override fun onSensorChanged(event: SensorEvent) {
        var fn = Throwable().stackTrace[0].methodName

        val sensorData = event.values[0]

        if(sensorData in 8.0..9.0 ){
            SharedDataHelper.lightMode = LightMode.DARK
            Logger.log(VerboseLevel.LOCATION, "$fn: dark mode")
        }
        else{
            SharedDataHelper.lightMode = LightMode.BRIGHT
            Logger.log(VerboseLevel.LOCATION, "$fn: bright mode")
        }

    }

    override fun onRebind(intent: Intent?) {
        // Register a listener for the sensor.
        super.onRebind(intent)
        sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        super.onUnbind(intent)
        // Be sure to unregister the sensor when the activity pauses.
        sensorManager.unregisterListener(this)
        return true
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun startMyOwnForeground() {
        var fn = Throwable().stackTrace[0].methodName
        Logger.log(VerboseLevel.INFO_ERR, "$fn: called.")

        val NOTIFICATION_CHANNEL_ID = "com.ezaf.www.CitizenScience"
        val channelName = "light update background service"
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
