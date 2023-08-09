package com.example.data.repository

import android.location.Location
import android.util.Log
import com.example.data.mapper.LatXLngY
import com.example.data.mapper.MapperToLocation
import com.example.data.mapper.convertGRID_GPS
import com.example.data.model.LocationResponse
import com.example.data.source.LocationSource
import com.example.domain.repository.LocationRepository
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(private val locationSource: LocationSource) :LocationRepository {
    private lateinit var gpsXY : LatXLngY
    override suspend fun getLocation(): com.example.domain.model.Location {
        val latlong = locationSource.getLocation()
        if (latlong != null) {
            gpsXY = convertGRID_GPS(0, latlong.latitude, latlong.longitude)
        }
        Log.d("gpsXY", "${gpsXY.x}, ${gpsXY.y}")
        val reponse = MapperToLocation(gpsXY)
        return reponse
    }

    override suspend fun getAddress(latitude: Double, longitude : Double, num : Int) : String?{
        return locationSource.getAddress(latitude,longitude, num)
    }
}