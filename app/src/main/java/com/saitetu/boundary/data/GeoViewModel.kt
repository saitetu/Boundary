package com.saitetu.boundary.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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