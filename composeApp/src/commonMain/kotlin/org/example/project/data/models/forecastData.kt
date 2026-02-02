package org.example.project.data.models


import kotlinx.serialization.Serializable


@Serializable
data class ForecastData(
    val dt: Long,
    val dt_txt: String,
    val main: Main,
    val weather: List<Weather> = emptyList(),

    val clouds: Clouds? = null,
    val wind: Wind? = null,
    val pop: Double? = null,
    val rain: Rain? = null,
    val sys: ForecastSys? = null,
    val visibility: Int? = null
)
