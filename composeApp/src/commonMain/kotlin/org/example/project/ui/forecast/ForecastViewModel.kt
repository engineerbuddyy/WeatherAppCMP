package org.example.project.ui.forecast


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.geo.LatLng
import dev.icerock.moko.geo.LocationTracker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.example.project.data.models.ForecastData
import org.example.project.data.models.ForecastResponse
import org.example.project.data.repo.WeatherRepository

class ForecastViewModel(val locationTracker: LocationTracker) : ViewModel() {

    val repository = WeatherRepository()
    private val _state = MutableStateFlow<ForecastState>(ForecastState.Loading)
    val state = _state.asStateFlow()

    fun getForecast() {
        viewModelScope.launch {
            _state.value = ForecastState.Loading
            try {
                val location = getCurrentLocation()
                val response = repository.fetchForecast(location)

                val dailyData = getDailyForecast(response)
                val weeklyData = getWeeklyForecast(response)
                    .mapNotNull { it.value.firstOrNull() }

                _state.value = ForecastState.Data(dailyData, weeklyData)
            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = ForecastState.Error(e)
            }
        }
    }


    private fun getDailyForecast(response: ForecastResponse): List<ForecastData> {
        val sortedData = response.list.sortedBy { it.dt }.map { it.dt_txt.split(" ")[0] }
        val groupedData = response.list.groupBy { it.dt_txt.split(" ")[0] }
        return groupedData[sortedData[0]] ?: emptyList()
    }

    private fun getWeeklyForecast(response: ForecastResponse): Map<String, List<ForecastData>> {
        val groupedData = response.list.sortedBy { it.dt }.groupBy { it.dt_txt.split(" ")[0] }
        return groupedData
    }

    private suspend fun getCurrentLocation(): LatLng {
        locationTracker.startTracking()
        val location = locationTracker.getLocationsFlow().first()
        locationTracker.stopTracking()
        return location
    }

}

sealed class ForecastState {
    object Loading : ForecastState()
    data class Data(val dailyData: List<ForecastData>, val weeklyDate: List<ForecastData>) :
        ForecastState()

    data class Error(val error: Throwable) : ForecastState()
}