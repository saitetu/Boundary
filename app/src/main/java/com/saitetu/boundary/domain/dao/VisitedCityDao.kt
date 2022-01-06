package com.saitetu.boundary.domain.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.saitetu.boundary.domain.entity.VisitedCity

@Dao
interface VisitedCityDao {
    @Query("SELECT * FROM visitedCity")
    fun getAll(): LiveData<List<VisitedCity>>

    @Insert
    fun insert(visitedCity: VisitedCity)

    @Update
    fun update(visitedCity: VisitedCity)

    @Delete
    fun delete(visitedCity: VisitedCity)
}