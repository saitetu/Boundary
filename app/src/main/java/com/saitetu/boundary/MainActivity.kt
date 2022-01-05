package com.saitetu.boundary

import android.Manifest.permission.*
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
import com.saitetu.boundary.data.GeoResponse
import com.saitetu.boundary.data.GeoViewModel
import com.saitetu.boundary.data.Response


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val geoViewModel = GeoViewModel()
        geoViewModel.load("140", "40")
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
        }) {
            Text(text = "Start")
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