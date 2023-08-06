package com.es.weatherprojects_tuk.Interface

import com.es.weatherprojects_tuk.data.WEATHER
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface WeatherInterface {
    @GET("getVilageFcst?serviceKey=sAbCBpNVQO2UjYOMlXoWTV3Uusxh92nCB5I1gwUOf2yjnOhZ9m6SuVeNPx0RY82nQC4sRp%2Fr4T06IGrW5%2BEc8A%3D%3D")
    suspend fun GetWeather(
        @Query("dataType") data_type: String,
        @Query("numOfRows") num_of_rows: Int,
        @Query("pageNo") page_no: Int,
        @Query("base_date") base_date: Int,
        @Query("base_time") base_time: String,
        @Query("nx") nx: Int,
        @Query("ny") ny: Int
    ): Response<WEATHER>
}

interface DayWeatherInterface {
    @GET("getUltraSrtFcst?serviceKey=sAbCBpNVQO2UjYOMlXoWTV3Uusxh92nCB5I1gwUOf2yjnOhZ9m6SuVeNPx0RY82nQC4sRp%2Fr4T06IGrW5%2BEc8A%3D%3D")
    suspend fun GetDayWeather(
        @Query("dataType") data_type: String,
        @Query("numOfRows") num_of_rows: Int,
        @Query("pageNo") page_no: Int,
        @Query("base_date") base_date: Int,
        @Query("base_time") base_time: String,
        @Query("nx") nx: Int,
        @Query("ny") ny: Int
    ): Response<WEATHER>
}

val gson : Gson = GsonBuilder()
    .setLenient()
    .create()

val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS) // 새로운 연결을 설정하는 데 걸리는 최대 시간
    .readTimeout(30, TimeUnit.SECONDS) // 데이터를 읽는 데 걸리는 최대 시간
    .writeTimeout(15, TimeUnit.SECONDS) // 데이터를 쓰는 데 걸리는 최대 시간
    .build()


val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/") // 마지막 / 반드시 들어가야 함
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create(gson)) // converter 지정
    .build() // retrofit 객체 생성