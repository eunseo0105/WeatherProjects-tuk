package com.example.data.api

import com.example.data.model.WEATHERRESPONSE
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("getVilageFcst?serviceKey=sAbCBpNVQO2UjYOMlXoWTV3Uusxh92nCB5I1gwUOf2yjnOhZ9m6SuVeNPx0RY82nQC4sRp%2Fr4T06IGrW5%2BEc8A%3D%3D")
    suspend fun GetWeather(
        @Query("dataType") data_type: String,
        @Query("numOfRows") num_of_rows: Int,
        @Query("pageNo") page_no: Int,
        @Query("base_date") base_date: Int,
        @Query("base_time") base_time: String,
        @Query("nx") nx: Int,
        @Query("ny") ny: Int
    ): WEATHERRESPONSE
}