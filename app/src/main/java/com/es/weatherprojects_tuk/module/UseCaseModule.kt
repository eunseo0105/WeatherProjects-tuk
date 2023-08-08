package com.es.weatherprojects_tuk.module

import com.example.domain.repository.LocationRepository
import com.example.domain.repository.WeatherRepository
import com.example.domain.usecase.LocationUseCase
import com.example.domain.usecase.WeatherUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideLocationUseCase(repository: LocationRepository): LocationUseCase {
        return LocationUseCase(repository)
    }

    @Provides
    fun provideWeatherUseCase(repository: WeatherRepository) : WeatherUseCase{
        return WeatherUseCase(repository)
    }
}
