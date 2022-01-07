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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.saitetu.boundary.data.*
import com.saitetu.boundary.model.entity.VisitedCity
import com.saitetu.boundary.viewmodel.GeoViewModel


class MainActivity : AppCompatActivity() {
    //    val geoViewModel = ViewModelProvider.NewInstanceFactory().create(GeoViewModel::class.java)
    private val geoViewModel by lazy {
        GeoViewModel(
            GeoRepository(this),
            VisitedCityRepository(VisitedCityDataBase.getInstance(this).visitedCityDao()),
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        geoViewModel.load("140", "40")
        createNotificationChannel()
        val context = this
        setContent {
            Column(
                Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SwitchWithText(
                    geoViewModel,
                    isLoggingOnNext = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            requestPermissionLauncher.launch(
                                arrayOf(
                                    ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION,
                                    WRITE_EXTERNAL_STORAGE, FOREGROUND_SERVICE
                                )
                            )
                        }
                        val intent = Intent(context, GpsService::class.java)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(intent)
                        }
                    },
                    isNotLoggingOnNext = {
                        val intent = Intent(context, GpsService::class.java)
                        stopService(intent)
                    })
                HelloScreen(geoViewModel.geo)
                VisitedList(geoViewModel)
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


}

@Composable
fun SwitchWithText(
    geoViewModel: GeoViewModel,
    isLoggingOnNext: () -> Unit,
    isNotLoggingOnNext: () -> Unit
) {
    Row(
        Modifier
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "越境監視をONにする",
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(16.dp),
            style = MaterialTheme.typography.subtitle1
        )
        Spacer(Modifier.weight(1f))
        val checkedState by geoViewModel.isLogging.observeAsState(false)
        Switch(
            checked = checkedState,
            onCheckedChange = {
                geoViewModel.changeLoggingState(it)
                if (it) {
                    isLoggingOnNext()
                } else {
                    isNotLoggingOnNext()
                }
            },
            modifier = Modifier.align(Alignment.CenterVertically)
        )
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


@Composable
fun VisitedList(geoViewModel: GeoViewModel) {
    val list by geoViewModel.visitedCities.observeAsState()
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        list?.forEach { it ->
            CityCard(it)
        }
    }

}

@Composable
fun CityCard(visitedCity: VisitedCity) {
    Text(visitedCity.prefecture)
    Text(visitedCity.city)
    Text(visitedCity.town)
    Text(visitedCity.time)
    Divider(
        color = Color.Blue
    )
}