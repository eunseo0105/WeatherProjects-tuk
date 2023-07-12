package com.es.weatherprojects_tuk.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.es.weatherprojects_tuk.data.WEATHER
import com.es.weatherprojects_tuk.Interface.WeatherInterface
import com.es.weatherprojects_tuk.Interface.retrofit
import kotlinx.coroutines.launch
import retrofit2.Response

class WeatherViewModel: ViewModel() {
    private val retrofitService: WeatherInterface by lazy { retrofit.create(WeatherInterface::class.java) }

    private val _weatherResponse : MutableLiveData<Response<WEATHER>> = MutableLiveData()
    val weatherResponse get() = _weatherResponse

    fun getWeather(data_type:String, num_of_rows:Int, page_no:Int, base_data:Int, base_time:Int, nx:String, ny:String){
        viewModelScope.launch {
            val response = retrofitService.GetWeather(data_type,num_of_rows, page_no, base_data, base_time, nx, ny)
            try {
                if (response.isSuccessful) {
                    weatherResponse.value = response

                }
            } catch (e: Exception) {
                e.message?.let { Log.d("api fail", it) }
            }

        }
    }



}