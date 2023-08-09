package com.es.weatherprojects_tuk.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.es.weatherprojects_tuk.R
import com.es.weatherprojects_tuk.adapter.RecyclerViewAdapter
import com.example.domain.model.ITEM
import com.example.domain.model.Weather
import com.example.domain.usecase.WeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(private val weatherUseCase: WeatherUseCase): ViewModel() {
    //단기예보 날씨
    private val _weatherResponse : MutableLiveData<Response<Weather>> = MutableLiveData()
    val weatherResponse get() = _weatherResponse
    //단기예보 날씨
    private val _dayWeatherResponse : MutableLiveData<Response<Weather>> = MutableLiveData()
    val dayWeatherResponse get() = _dayWeatherResponse

    //오늘 날씨
    private val _todayData : MutableLiveData<List<ITEM>> = MutableLiveData()
    val todayData :LiveData<List<ITEM>> get() = _todayData
    //내일 날씨
    private val _tomorrowData : MutableLiveData<List<ITEM>> = MutableLiveData()
    val tomorrowData : LiveData<List<ITEM>> get() = _tomorrowData

    //단기예보 가져오기
    fun getWeather(data_type:String, num_of_rows:Int, page_no:Int, base_date:Int, base_time:String, nx:Int, ny:Int){
        viewModelScope.launch {
            try {
                val result = weatherUseCase.getWeather(data_type, num_of_rows, page_no, base_date, base_time, nx, ny)
                Log.d("result", result.toString())
                _weatherResponse.postValue(Response.success(result))
            }
            catch (e:Exception){
                Log.d("api fail ", "${e.message}")
            }
        }
    }

    //초단기예보 가져오기
    fun getDayWeather(data_type:String, num_of_rows:Int, page_no:Int, base_date:Int, base_time:String, nx:Int, ny:Int){
        viewModelScope.launch {
            try {
                val result = weatherUseCase.getDayWeather(data_type, num_of_rows, page_no, base_date, base_time, nx, ny)
                Log.d("result", result.toString())
                _dayWeatherResponse.postValue(Response.success(result))

            }catch (e: Exception){
                Log.d("day api fail", "${e.message}")
            }
        }
    }


    //오늘 날씨 필터링
    fun todayWeather(weatherData: Weather,hour:Int, baseDate :Int) {
        // 'TMP' 카테고리이고, 현재 시간부터 12시간 동안의 데이터만 필터링
        val filteredData = weatherData.response.body.items.item.filter {
            it.category == "TMP" && it.fcstDate== baseDate.toString() && it.fcstTime.toInt() in hour..(hour + 2400)
        }

        Log.d("filteredData", filteredData.toString())
        _todayData.postValue(filteredData)

    }

    //내일 날씨 필터링
    fun tomorrowWeather(weatherData: Weather, nextDate : Int){
        // fastdate가 내일이고, 모든 날짜 출력
        val filteredData = weatherData.response.body.items.item.filter {
            it.fcstDate.toInt() == nextDate && it.category == "TMP"
        }
        Log.d("filteredData", filteredData.toString())
        _tomorrowData.postValue(filteredData)
    }

}