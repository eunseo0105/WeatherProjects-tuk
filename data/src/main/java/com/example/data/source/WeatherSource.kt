package com.example.data.source

import com.example.data.model.WEATHERRESPONSE

interface WeatherSource {
    suspend fun getWeather(data_type: String, num_of_rows: Int, page_no: Int, base_date: Int, base_time: String, nx: Int, ny: Int): WEATHERRESPONSE

    suspend fun getDayWeather(data_type:String, num_of_rows:Int, page_no:Int, base_date:Int, base_time:String, nx:Int, ny:Int) : WEATHERRESPONSE
}