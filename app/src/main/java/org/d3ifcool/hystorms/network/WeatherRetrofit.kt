package org.d3ifcool.hystorms.network

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherRetrofit {
    @GET("weather")
    suspend fun get(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") appId: String,
        @Query("lang") language: String
    ): WeatherEntity
}