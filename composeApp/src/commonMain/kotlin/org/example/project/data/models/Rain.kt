package org.example.project.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Rain(
    @SerialName("1h") val oneHour: Double? = null,
    @SerialName("3h") val threeHour: Double? = null
)
