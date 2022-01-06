package com.saitetu.boundary.domain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class VisitedCity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var prefecture: String,
    var city: String,
    var town: String,
    var time: LocalDateTime
)
