package com.saitetu.boundary.data

data class GeoResponse(
    val response: Response
)

data class Response(
    val location: List<Location>
)

data class Location(
    val city: String,
    val city_kana: String,
    val town: String,
    val town_kana: String,
    val x: Double,
    val y: Double,
    val distance: Float,
    val prefecture: String,
    val postal: String
)
