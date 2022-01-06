package com.saitetu.boundary.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.saitetu.boundary.model.entity.VisitedCity

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