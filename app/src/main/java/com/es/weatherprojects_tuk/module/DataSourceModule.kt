package com.es.weatherprojects_tuk.module

import com.example.data.source.LocationSource
import com.example.data.source.LocationSourceImpl
import com.example.data.source.WeatherSource
import com.example.data.source.WeatherSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    abstract fun bindLocationDataSource(locationSourceImpl: LocationSourceImpl):LocationSource

    @Binds
    abstract fun bindWeatherDataSource(weatherSourceImpl: WeatherSourceImpl) :WeatherSource
}