package com.es.weatherprojects_tuk.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Weather
import com.example.domain.usecase.WeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(private val weatherUseCase: WeatherUseCase): ViewModel() {
    private val _weatherResponse : MutableLiveData<Response<Weather>> = MutableLiveData()
    val weatherResponse get() = _weatherResponse

    private val _dayWeatherResponse : MutableLiveData<Response<Weather>> = MutableLiveData()
    val dayWeatherResponse get() = _dayWeatherResponse


    fun getWeather(data_type:String, num_of_rows:Int, page_no:Int, base_date:Int, base_time:String, nx:Int, ny:Int){
        viewModelScope.launch {
            try {
                val result = weatherUseCase.getWeather(data_type, num_of_rows, page_no, base_date, base_time, nx, ny)
                Log.d("result", result.response.header.resultMsg)
                _weatherResponse.postValue(Response.success(result))
            }
            catch (e:Exception){
                Log.d("api fail ", "${e.message}")
            }
        }
    }

//    fun getDayWeather(data_type:String, num_of_rows:Int, page_no:Int, base_date:Int, base_time:String, nx:Int, ny:Int){
//        viewModelScope.launch {
//            val dayresponse = retrofitService2.GetDayWeather(data_type,num_of_rows, page_no, base_date, base_time, nx, ny)
//            try {
//                if(dayresponse.isSuccessful){
//                    dayWeatherResponse.value= dayresponse
//                }
//            }catch (e:Exception){
//                e.message?.let { Log.d("day api fail", it) }
//            }
//        }
//    }

    fun getDayWeather(data_type:String, num_of_rows:Int, page_no:Int, base_date:Int, base_time:String, nx:Int, ny:Int){
        viewModelScope.launch {
            try {
                val result = weatherUseCase.getDayWeather(data_type, num_of_rows, page_no, base_date, base_time, nx, ny)
                Log.d("result", result.response.header.resultMsg)
                _dayWeatherResponse.postValue(Response.success(result))

            }catch (e: Exception){
                Log.d("day api fail", "${e.message}")
            }
        }
    }



}