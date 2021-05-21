package org.d3ifcool.hystorms.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Weather(
    val id: Int,
    val description: String,
    val icon: String,
    val temp: Long,
    val feelsLike: Long,
    val pressure: Long,
    val humidity: Long,
    val windSpeed: Long,
    val windDegree: Long
) : Parcelable
