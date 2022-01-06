package com.saitetu.boundary

import android.Manifest
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.saitetu.boundary.data.GeoRepository

class GpsService : Service() {
    companion object {
        const val CHANNEL_ID = "777"
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val repository = GeoRepository(this)

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun locationRequest() {

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var updatedCount = 0
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                for (location in p0.locations) {
                    updatedCount++
                    repository.getLocation(
                        location.longitude.toString(),
                        location.latitude.toString()
                    ) {
                        Log.d(
                            this.javaClass.name,
                            it
                        )
                    }
                }
            }
        }

        val openIntent = Intent(this, MainActivity::class.java).let {
            PendingIntent.getActivity(this, 0, it, FLAG_IMMUTABLE)
        }
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("boundary")
            .setContentText("位置情報を取得しています...")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(openIntent)
            .build()

        startForeground(9999, notification)

        startLocationUpdates()

        return START_STICKY
    }


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun stopService(name: Intent?): Boolean {
        stopLocationUpdates()
        return super.stopService(name)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
        stopSelf()
    }

    private fun startLocationUpdates() {
        val locationRequest = createLocationRequest()
        Looper.myLooper()?.let {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                it
            )
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

}