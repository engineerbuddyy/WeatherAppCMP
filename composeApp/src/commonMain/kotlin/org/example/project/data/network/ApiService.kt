package org.example.project.data.network

import dev.icerock.moko.geo.LatLng
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.project.data.models.ForecastData
import org.example.project.data.models.ForecastResponse
import org.example.project.data.models.WeatherResponse

class ApiService {


    private val httpClient= HttpClient {
        install(ContentNegotiation){
            json(Json{
                encodeDefaults=true
                isLenient=true
                coerceInputValues=true
                ignoreUnknownKeys=true
            })

        }
    }

    suspend fun getWeather(location: LatLng): WeatherResponse {
        return httpClient.get("${BASE_URL}") {
            url {
                parameters.append("lat", location.latitude.toString())
                parameters.append("lon", location.longitude.toString())
                parameters.append("appid", API_KEY)
                parameters.append("units", "metric")

            }

        }.body()
    }

    suspend fun getForecast(location:LatLng): ForecastResponse {
        return httpClient.get("${BASE_URL_FORCAST}") {
            url {
                parameters.append("lat", location.latitude.toString())
                parameters.append("lon", location.longitude.toString())
                parameters.append("appid", API_KEY)
                parameters.append("units", "metric")
            }
        }.body()
    }

}