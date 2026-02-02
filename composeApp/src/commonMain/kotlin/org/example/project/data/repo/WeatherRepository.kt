package org.example.project.data.repo

import dev.icerock.moko.geo.LatLng
import org.example.project.data.models.ForecastResponse
import org.example.project.data.models.WeatherResponse
import org.example.project.data.network.ApiService

class WeatherRepository {
    private val apiService = ApiService()

    suspend fun fetchWeather(location: LatLng): WeatherResponse =
        apiService.getWeather(location)

    suspend fun fetchForecast(location: LatLng): ForecastResponse =
        apiService.getForecast(location)
}
