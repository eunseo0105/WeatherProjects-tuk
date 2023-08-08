package com.es.weatherprojects_tuk.module

import com.example.data.api.DayWeatherAPi
import com.example.data.api.WeatherApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class WeatherModule {

    @Singleton
    @Provides
    fun provideWeatherApi(retrofit: Retrofit): WeatherApi {
        return retrofit.create(WeatherApi::class.java)
    }

    @Singleton
    @Provides
    fun provideDayWeatherApi(retrofit: Retrofit) : DayWeatherAPi{
        return retrofit.create(DayWeatherAPi::class.java)
    }

}