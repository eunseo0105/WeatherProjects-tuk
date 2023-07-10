package com.es.weatherprojects_tuk.Interface

import com.es.weatherprojects_tuk.data.WEATHER
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherInterface {
    @GET("getVilageFcst?serviceKey=sAbCBpNVQO2UjYOMlXoWTV3Uusxh92nCB5I1gwUOf2yjnOhZ9m6SuVeNPx0RY82nQC4sRp%2Fr4T06IGrW5%2BEc8A%3D%3D")
    suspend fun GetWeather(
        @Query("dataType") data_type : String,
        @Query("numOfRows") num_of_rows : Int,
        @Query("pageNo") page_no : Int,
        @Query("base_date") base_date : Int,
        @Query("base_time") base_time : Int,
        @Query("nx") nx : String,
        @Query("ny") ny : String
    ): Response<WEATHER>
}

val gson : Gson = GsonBuilder()
    .setLenient()
    .create()

val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/") // 마지막 / 반드시 들어가야 함
    .addConverterFactory(GsonConverterFactory.create(gson)) // converter 지정
    .build() // retrofit 객체 생성