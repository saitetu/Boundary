package com.saitetu.boundary.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saitetu.boundary.data.GeoRepository
import com.saitetu.boundary.data.GeoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GeoViewModel : ViewModel() {
    var geo: MutableLiveData<GeoResponse> = MutableLiveData<GeoResponse>()
    private val repository = GeoRepository()

    fun load(x:String,y:String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getLocations(x, y)?.let {
                geo.postValue(it)
            }
        }
    }

}