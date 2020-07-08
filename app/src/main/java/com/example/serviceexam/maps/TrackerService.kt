package com.example.serviceexam.maps

import android.Manifest
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import com.example.serviceexam.R
import com.google.android.gms.location.*
import com.google.gson.GsonBuilder
import timber.log.Timber
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

@Suppress("IMPLICIT_CAST_TO_ANY")
open class TrackerService : Service() {

    private val binder = LocalBinder()

    private var client: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var location: Location? = null
    private var listLocations = ArrayList<String>()

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        buildNotification()
        setupMap()
        requestLocationUpdates()
    }

    inner class LocalBinder : Binder() {
        // Return this instance so clients can call public methods
        fun getService(): TrackerService = this@TrackerService
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildNotification() {
        val stop = "stop"
        registerReceiver(stopReceiver, IntentFilter(stop))
        val broadcastIntent = PendingIntent.getBroadcast(
            this, 0, Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT
        )
        // Create the persistent notification
        val channelId = createNotificationChannel("my_service", "My Background Service")

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.notification_text))
            .setOngoing(true)
            .setContentIntent(broadcastIntent)
            .setSmallIcon(R.drawable.logo)
        startForeground(1, builder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    private var stopReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            Log.d(TAG, "received stop broadcast")
            // Stop the service when the notification is tapped
            unregisterReceiver(this)
            stopSelf()
        }
    }

    private fun requestLocationUpdates() {
        val request = LocationRequest()
        request.interval = 20000
        request.fastestInterval = 5000
        request.priority = LocationRequest.PRIORITY_LOW_POWER
        request.maxWaitTime = 30000
        client = LocationServices.getFusedLocationProviderClient(this)

        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permission == PackageManager.PERMISSION_GRANTED) {
            client?.requestLocationUpdates(request,locationCallback, null)
        }
    }

    private fun setupMap() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                Toast.makeText(applicationContext, "Tracking enable", Toast.LENGTH_LONG).show()
                location = locationResult.lastLocation
                if (location != null) {
                    listLocations.add("Latitude: ${location?.latitude.toString()} / Longitude: ${location?.longitude.toString()}")
                    Log.d("Database", "${location?.longitude} and ${location?.latitude} ")
                }
            }
        }
    }

    fun stopTracker(): List<String?> {
        unregisterReceiver(stopReceiver)
        client?.removeLocationUpdates(locationCallback)
        Log.d("Service", "stoped")
        stopSelf()
        return listLocations
    }

    companion object {
        private val TAG = TrackerService::class.java.simpleName
    }
}