package com.example.myweather

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myweather.ui.theme.MyWeatherTheme
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {
    private var locationManager : LocationManager? = null
    private lateinit var mViewmodel: MainViewmodel
    companion object{
        val API_KEY = "cbb5e256928e47458c7102715253001"
    }
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mViewmodel = ViewModelProvider(this).get(MainViewmodel::class.java)
        window.decorView.windowInsetsController?.setSystemBarsAppearance( 0, APPEARANCE_LIGHT_STATUS_BARS)
        initObeservers()
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            Toast.makeText(applicationContext,"Permission Disabled", Toast.LENGTH_SHORT).show()
            return
        }
        locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)

    }
    fun setComposeView(){
        setContent {
            MyWeatherTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeView(mViewmodel)
                }
            }
        }
    }
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            mViewmodel.latitude = location.latitude.toFloat()
            mViewmodel.longitude = location.longitude.toFloat()
            mViewmodel.city = getCityName(location.latitude,location.longitude)
            mViewmodel.fetchForeCastData()

        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private fun getCityName(lat: Double,long: Double):String{
        val cityName: String?
        val geoCoder = Geocoder( applicationContext , Locale.getDefault())
        val Adress = geoCoder.getFromLocation(lat,long,3)

        cityName = Adress?.get(0)?.locality ?:""
        return cityName
    }
     fun initObeservers(){
         mViewmodel.viewModelScope.launch {


        mViewmodel.weaterFlow.collect {
            when(it){
                WeatherFlows.Success ->{
                    setComposeView()
                }

                is WeatherFlows.Toast -> {

                }
            }
        }
        }
    }
}

