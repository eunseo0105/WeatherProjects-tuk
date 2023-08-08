package com.example.data.repository

import com.example.data.mapper.MapperToWeather
import com.example.data.source.WeatherSource
import com.example.domain.model.Weather
import com.example.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(private val weatherSource: WeatherSource) : WeatherRepository{
    override suspend fun getWeather(data_type: String, num_of_rows: Int, page_no: Int, base_date: Int, base_time: String, nx: Int, ny: Int): Weather {
        val response =  weatherSource.getWeather(data_type, num_of_rows, page_no, base_date, base_time, nx, ny)
        val result = MapperToWeather(response)
        return result
    }

    override suspend fun getDayWeather(data_type: String, num_of_rows: Int, page_no: Int, base_date: Int, base_time: String, nx: Int, ny: Int): Weather {
        val response = weatherSource.getDayWeather(data_type, num_of_rows, page_no, base_date, base_time, nx, ny)
        val result = MapperToWeather(response)
        return result
    }
}