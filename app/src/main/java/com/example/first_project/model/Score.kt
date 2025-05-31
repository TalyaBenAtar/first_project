package com.example.first_project.model

import java.time.LocalDate
import kotlin.apply

data class Score private constructor(
    val score: Int,
    val releaseDate: String,
    val lat: Double,
    val lon: Double
) {

    class Builder(
        var score: Int = 0,
        var releaseDate: String = LocalDate.now().toString(),
        var lat: Double = 0.0,
        var lon: Double = 0.0
    ) {
        fun score(score: Int) = apply { this.score = score }
        fun releaseDate(releaseDate: LocalDate) = apply { this.releaseDate = releaseDate.toString() }
        fun lat(lat: Double) = apply { this.lat = lat }
        fun lon(lon: Double) = apply { this.lon = lon }

        fun build() = Score(score, releaseDate, lat, lon)

    }
}
