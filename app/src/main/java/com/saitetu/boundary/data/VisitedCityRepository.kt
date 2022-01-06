package com.saitetu.boundary.data

import androidx.lifecycle.LiveData
import com.saitetu.boundary.domain.dao.VisitedCityDao
import com.saitetu.boundary.domain.entity.VisitedCity

/**
 * SqLite用repository
 */
//TODO Hilt経由でinjectして毎回インスタンスを作成しないようにしてください
class VisitedCityRepository(private val dao: VisitedCityDao) {

    fun insertVisitedCity(visitedCity: VisitedCity) {
        dao.insert(visitedCity)
    }

    fun getVisitedCityList(): LiveData<List<VisitedCity>> {
        return dao.getAll()
    }
}