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

    init {
        load()
    }

    fun load() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getLocations("135", "35")?.let {
                geo.postValue(it)
            }
        }
    }

}