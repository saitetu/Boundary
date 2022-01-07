package com.saitetu.boundary.data

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.saitetu.boundary.model.entity.VisitedCity
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit


interface GeoApiInterface {
    @GET("json")
    fun getGeoLocationList(
        @Query("method") method: String,
        @Query("x") x: String,
        @Query("y") y: String
    ): Call<GeoResponse>
}

class GeoRepository(context: Context) {
    private var retrofit: Retrofit
    private val visitedCityRepository =
        VisitedCityRepository(VisitedCityDataBase.getInstance(context).visitedCityDao())

    init {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        this.retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(getClient())
            .build()
    }

    fun getLocations(x: String, y: String): GeoResponse? {
        val service = this.retrofit.create(GeoApiInterface::class.java)
        try {
            val response = service.getGeoLocationList(METHOD, x, y).execute()
            if (response.isSuccessful) {
                return response.body()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getLocation(x: String, y: String, success: (city: String) -> Unit) {
        val service = this.retrofit.create(GeoApiInterface::class.java)
        service.getGeoLocationList(METHOD, x, y).enqueue(
            object : Callback<GeoResponse> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<GeoResponse>,
                    response: Response<GeoResponse>
                ) {
                    response.body()?.let { geoResponse ->
                        val visitedCity = geoResponse.response.location[0].let {
                            VisitedCity(
                                id = 0,
                                prefecture = it.prefecture,
                                city = it.city,
                                town = it.town,
                                time = LocalDateTime.now().toString()
                            )
                        }
                        GlobalScope.launch {
                            visitedCityRepository.getLatestVisitedCity().let {
                                if (it.city != visitedCity.city) {
                                    visitedCityRepository.insertVisitedCity(visitedCity)
                                }
                            }
                        }
                    }
                    response.body()?.response?.location?.get(0)?.let { success(it.city) }
                }

                override fun onFailure(call: Call<GeoResponse>, t: Throwable) {
                }
            })
    }

    private fun getClient(): OkHttpClient {
        return OkHttpClient
            .Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    companion object {
        const val METHOD = "searchByGeoLocation"
        const val URL = "https://geoapi.heartrails.com/api/"
    }

}