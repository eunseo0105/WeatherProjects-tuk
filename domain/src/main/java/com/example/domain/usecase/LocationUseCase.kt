package com.example.domain.usecase

import android.location.Location
import com.example.domain.repository.LocationRepository
import javax.inject.Inject


class LocationUseCase @Inject constructor(private val locationRepository: LocationRepository) {
    suspend fun getLocation() : com.example.domain.model.Location {
        return locationRepository.getLocation()
    }

}