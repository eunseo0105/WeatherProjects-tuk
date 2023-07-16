package com.es.weatherprojects_tuk.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.es.weatherprojects_tuk.Interface.DayWeatherInterface
import com.es.weatherprojects_tuk.data.WEATHER
import com.es.weatherprojects_tuk.Interface.WeatherInterface
import com.es.weatherprojects_tuk.Interface.retrofit
import kotlinx.coroutines.launch
import retrofit2.Response

class WeatherViewModel: ViewModel() {
    private val retrofitService: WeatherInterface by lazy { retrofit.create(WeatherInterface::class.java) }
    private val retrofitService2: DayWeatherInterface by lazy { retrofit.create(DayWeatherInterface::class.java) }
    private val _weatherResponse : MutableLiveData<Response<WEATHER>> = MutableLiveData()
    val weatherResponse get() = _weatherResponse

    private val _dayWeatherResponse : MutableLiveData<Response<WEATHER>> = MutableLiveData()
    val dayWeatherResponse get() = _dayWeatherResponse

    fun getWeather(data_type:String, num_of_rows:Int, page_no:Int, base_date:Int, base_time:String, nx:Int, ny:Int){
        viewModelScope.launch {
            val response = retrofitService.GetWeather(data_type,num_of_rows, page_no, base_date, base_time, nx, ny)
            try {
                if (response.isSuccessful) {
                    weatherResponse.value = response

                }
            } catch (e: Exception) {
                e.message?.let { Log.d("api fail", it) }
            }

        }
    }

    fun getDayWeather(data_type:String, num_of_rows:Int, page_no:Int, base_date:Int, base_time:String, nx:Int, ny:Int){
        viewModelScope.launch {
            val dayresponse = retrofitService2.GetDayWeather(data_type,num_of_rows, page_no, base_date, base_time, nx, ny)
            try {
                if(dayresponse.isSuccessful){
                    dayWeatherResponse.value= dayresponse
                }
            }catch (e:Exception){
                e.message?.let { Log.d("day api fail", it) }
            }
        }
    }



}