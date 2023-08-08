package com.example.data.source

import com.example.data.api.DayWeatherAPi
import com.example.data.api.WeatherApi
import com.example.data.model.WEATHERRESPONSE
import javax.inject.Inject

class WeatherSourceImpl @Inject constructor(private val weatherApi: WeatherApi, private val dayWeatherAPi: DayWeatherAPi): WeatherSource{
    override suspend fun getWeather(data_type: String, num_of_rows: Int, page_no: Int, base_date: Int, base_time: String, nx: Int, ny: Int): WEATHERRESPONSE {
        return weatherApi.GetWeather(data_type, num_of_rows, page_no, base_date, base_time, nx, ny)
    }

    override suspend fun getDayWeather(data_type: String, num_of_rows: Int, page_no: Int, base_date: Int, base_time: String, nx: Int, ny: Int): WEATHERRESPONSE {
        return dayWeatherAPi.GetDayWeather(data_type, num_of_rows, page_no, base_date, base_time, nx, ny)
    }
}