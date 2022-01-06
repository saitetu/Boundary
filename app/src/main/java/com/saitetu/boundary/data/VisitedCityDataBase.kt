package com.saitetu.boundary.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.saitetu.boundary.domain.dao.VisitedCityDao
import com.saitetu.boundary.domain.entity.VisitedCity

@Database(entities = [VisitedCity::class], version = 1)
abstract class VisitedCityDataBase : RoomDatabase() {

    abstract fun visitedCityDao(): VisitedCityDao

    companion object {

        private var INSTANCE: VisitedCityDataBase? = null

        private val lock = Any()

        fun getInstance(context: Context): VisitedCityDataBase {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        VisitedCityDataBase::class.java, "VisitedCity.db"
                    )
                        .allowMainThreadQueries()
                        .build()
                }
                return INSTANCE!!
            }
        }
    }
}