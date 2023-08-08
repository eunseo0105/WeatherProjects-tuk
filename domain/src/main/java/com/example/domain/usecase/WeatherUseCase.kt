package com.example.domain.usecase

import com.example.domain.model.Weather
import com.example.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherUseCase @Inject constructor(private val weatherRepository: WeatherRepository){
    suspend fun getWeather(data_type:String, num_of_rows:Int, page_no:Int, base_date:Int, base_time:String, nx:Int, ny:Int) : Weather {
        return weatherRepository.getWeather(data_type, num_of_rows, page_no, base_date, base_time, nx, ny)
    }

    suspend fun getDayWeather(data_type:String, num_of_rows:Int, page_no:Int, base_date:Int, base_time:String, nx:Int, ny:Int): Weather{
        return weatherRepository.getDayWeather(data_type, num_of_rows, page_no, base_date, base_time, nx, ny)
    }
}