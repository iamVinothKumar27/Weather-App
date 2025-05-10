package com.example.myweather

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    val baseUrl = "http://api.weatherapi.com/"
    val client = OkHttpClient.Builder()
        .addInterceptor{chain->
            val req = chain.request().newBuilder()
                .addHeader("User-Agent", "WeatherAPI")
                .build()
            chain.proceed(req)
        }
        .build()

   fun getInstance(): Retrofit {
       return Retrofit.Builder()
           .baseUrl(baseUrl)
           .client(client)
           .addConverterFactory(GsonConverterFactory.create())
           .build()
   }
}