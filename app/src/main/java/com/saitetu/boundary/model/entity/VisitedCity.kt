package com.saitetu.boundary.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class VisitedCity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var prefecture: String,
    var city: String,
    var town: String,
    var time: String,
    var latitude: Double,
    var longitude: Double
)
