package com.example.domain.repository

import com.example.domain.model.Weather

interface WeatherRepository {
    suspend fun getWeather(data_type:String, num_of_rows:Int, page_no:Int, base_date:Int, base_time:String, nx:Int, ny:Int): Weather

    suspend fun getDayWeather(data_type:String, num_of_rows:Int, page_no:Int, base_date:Int, base_time:String, nx:Int, ny:Int) : Weather
}