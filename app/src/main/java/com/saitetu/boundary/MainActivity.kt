package com.saitetu.boundary

import android.Manifest.permission.*
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.saitetu.boundary.data.GeoResponse
import com.saitetu.boundary.data.Response
import com.saitetu.boundary.viewmodel.GeoViewModel


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val geoViewModel = ViewModelProvider.NewInstanceFactory().create(GeoViewModel::class.java)
        geoViewModel.load("140", "40")
        createNotificationChannel()
        setContent {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                StartButton()
                HelloScreen(geoViewModel.geo)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { granted ->
        val notPermission = granted.filter {
            !it.value
        }.size
        if (notPermission != 0) {
            //NOT Permission
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                GpsService.CHANNEL_ID,
                "お知らせ",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "お知らせを通知します。"
            }
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @Composable
    fun StartButton() {
        Button(onClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                requestPermissionLauncher.launch(
                    arrayOf(
                        ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION,
                        WRITE_EXTERNAL_STORAGE, FOREGROUND_SERVICE
                    )
                )
            }
            val intent = Intent(this, GpsService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            }
        }) {
            Text(text = "Start")
        }
    }

    @Composable
    fun EndButton() {
        Button(onClick = {
            val intent = Intent(this, GpsService::class.java)
            stopService(intent)
        }) {
            Text("End")
        }
    }

    @Composable
    fun HelloScreen(geoLiveData: LiveData<GeoResponse>) {
        val geo by geoLiveData.observeAsState(GeoResponse(Response(emptyList())))
        if (geo.response.location.isNotEmpty()) {
            Loaded(city = geo.response.location[0].city)
        }
    }

    @Composable
    fun Loaded(city: String) {
        Text(
            text = city
        )
    }

}