package com.saitetu.boundary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.LiveData
import com.saitetu.boundary.data.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val geoViewModel: GeoViewModel = GeoViewModel()
        setContent {
            HelloScreen(geoViewModel.geo)
        }
    }

    @Composable
    fun HelloScreen(geoLiveData:LiveData<GeoResponse>) {
        val geo by geoLiveData.observeAsState(GeoResponse(Response(emptyList())))
        if (geo.response.location.isNotEmpty()){
            Loaded(city = geo.response.location[0].city)
        }
    }

    @Composable
    fun Loaded(city:String){
        setContent {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = city
                )
            }
        }
    }

}