package com.example.myweather

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myweather.model.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewmodel :ViewModel() {
    var latitude by mutableStateOf(0f)
    var longitude by mutableStateOf(0f)
    var city by mutableStateOf("")
    val service = RetrofitHelper.getInstance().create(RetrofitService::class.java)
     val  _weatherFlow = MutableSharedFlow<WeatherFlows>(replay = 0)
     val weaterFlow : MutableSharedFlow<WeatherFlows> = _weatherFlow
    val data  : MutableState<WeatherData?> = mutableStateOf<WeatherData?>(null)
    var selectedIndex by mutableStateOf(0)
    fun fetchForeCastData() {
        viewModelScope.launch(Dispatchers.Main) {

            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            val currentDate =date.format(Date())
            val response = service.getWeatherData(
                MainActivity.API_KEY,
                q = city
            )
            if(response.isSuccessful){
                weaterFlow.emit(WeatherFlows.Success)
                data.value = response.body()
                weaterFlow.emit(
                    WeatherFlows.Toast(response.body().toString())
                )
            }
            else{
                weaterFlow.emit(
                    WeatherFlows.Toast(response.errorBody().toString())
                )

            }
        }
    }


}