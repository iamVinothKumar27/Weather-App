package com.example.myweather

import com.example.myweather.model.WeatherData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Date

interface RetrofitService {
    @GET("v1/forecast.json")
    suspend fun getWeatherData(
        @Query("key") key :String,
        @Query("q") q :String,
        @Query("days") days : Int=7,
        @Query("api") api : String = "no",
        @Query("alerts") alerts : String = "no"

    ): Response<WeatherData>
}