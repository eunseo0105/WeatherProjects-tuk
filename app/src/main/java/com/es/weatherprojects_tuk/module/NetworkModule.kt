package com.es.weatherprojects_tuk.module

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {


    val gson : Gson = GsonBuilder()
        .setLenient()
        .create()

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)// 데이터를 읽는 데 걸리는 최대 시간
            .connectTimeout(30, TimeUnit.SECONDS)// 새로운 연결을 설정하는 데 걸리는 최대 시간
            .writeTimeout(15, TimeUnit.SECONDS) // 데이터를 쓰는 데 걸리는 최대 시간
            .build()
    }


    @Provides
    fun provideRetrofit(okHttp: OkHttpClient) : Retrofit {
        return Retrofit.Builder().apply {
            baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/") // 마지막 / 반드시 들어가야 함
            addConverterFactory(GsonConverterFactory.create(gson))
            client(okHttp)
        }.build()
    }

}