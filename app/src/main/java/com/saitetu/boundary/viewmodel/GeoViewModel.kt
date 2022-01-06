package com.saitetu.boundary.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saitetu.boundary.data.GeoRepository
import com.saitetu.boundary.data.GeoResponse
import com.saitetu.boundary.data.VisitedCityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GeoViewModel(
    private val geoRepository: GeoRepository,
    visitedCityRepository: VisitedCityRepository
) : ViewModel() {
    var geo: MutableLiveData<GeoResponse> = MutableLiveData<GeoResponse>()
    var visitedCities = visitedCityRepository.getVisitedCityList()

    fun load(x: String, y: String) {
        viewModelScope.launch(Dispatchers.IO) {
            geoRepository.getLocations(x, y)?.let {
                geo.postValue(it)
            }
        }
    }
}