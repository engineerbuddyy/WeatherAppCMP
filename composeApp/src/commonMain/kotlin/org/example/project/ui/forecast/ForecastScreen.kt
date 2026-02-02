package org.example.project.ui.forecast


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import dev.icerock.moko.geo.compose.BindLocationTrackerEffect
import dev.icerock.moko.geo.compose.LocationTrackerAccuracy
import dev.icerock.moko.geo.compose.rememberLocationTrackerFactory
import org.example.project.data.models.ForecastData
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import weatherapp.composeapp.generated.resources.Res
import weatherapp.composeapp.generated.resources.ic_cloud

@Composable
fun ForecastScreen(navController: NavController) {
    val factory = rememberLocationTrackerFactory(LocationTrackerAccuracy.Best)
    val locationTracker = remember { factory.createLocationTracker() }
    BindLocationTrackerEffect(locationTracker)
    val viewModel: ForecastViewModel = viewModel { ForecastViewModel(locationTracker) }

    val state = viewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.getForecast()
    }
    Column(
        modifier = Modifier.fillMaxSize().background(
            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF7FD4FF),
                    Color(0xFF4A90E2)
                )
            )
        ).systemBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Text(
                text = "Forecast",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }


        when (val forecastState = state.value) {
            is ForecastState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Text(
                        text = "Loading...",
                        color = Color.White
                    )
                }
            }

            is ForecastState.Data -> {
                // Data state
                val dailyData = forecastState.dailyData
                val weeklyData = forecastState.weeklyDate
                ForecastScreenContent(dailyData, weeklyData)
            }

            is ForecastState.Error -> {
                Text(
                    text = forecastState.error.message ?: forecastState.error.toString(),
                    color = Color.White
                )
            }

        }
    }

}

@Composable
fun ColumnScope.ForecastScreenContent(
    dailyData: List<ForecastData>,
    weeklyData: List<ForecastData>
) {

    SectionHeader(
        title = "Today",
        date = dailyData
            .firstOrNull()
        ?.dt_txt
        ?.substringBefore(" ")
        ?.split("-")
        ?.let { "${it[2]}-${it[1]}-${it[0]}" }

    )

    LazyRow(
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {
        items(dailyData) { data ->
            ForecastRowItem(data)
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    SectionHeader(title = "Next Days", date = null)

    LazyColumn(
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {
        items(weeklyData) { data ->
            ForecastColumnItem(data)
        }
    }
}

@Composable
fun SectionHeader(title: String, date: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        date?.let {
            Text(
                text = it,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}



@Composable
fun ForecastRowItem(data: ForecastData) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            //.width(110.dp)
            .size(120.dp)
            .background(
                Color.White.copy(alpha = 0.15f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = data.dt_txt.split(" ")[1].removeSuffix(":00"),
            fontSize = 18.sp,
            color = Color.White.copy(alpha = 0.8f)
        )

        Image(
            painter = painterResource(
                getImage(data.weather.getOrNull(0)?.main ?: "")
            ),
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )

        Text(
            text = "${data.main.temp?.toInt()}°",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.White
        )
    }
}



fun getImage(data: String): DrawableResource {
    return if (data.lowercase().contains("rain")) {
        Res.drawable.ic_cloud
    } else if (data.lowercase().contains("cloud")) {
        Res.drawable.ic_cloud
    } else {
        Res.drawable.ic_cloud
    }
}

@Composable
fun ForecastColumnItem(data: ForecastData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .size(80.dp)
            .background(
                Color.White.copy(alpha = 0.12f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = data.dt_txt
                .substringBefore(" ")
                .split("-")
                .let { "${it[2]}-${it[1]}-${it[0]}" },
            color = Color.White,
            fontSize = 16.sp
        )


        Image(
            painter = painterResource(
                getImage(data.weather.getOrNull(0)?.main ?: "")
            ),
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )

        Text(
            text = "${data.main.temp?.toInt()}°",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.White
        )
    }
}


