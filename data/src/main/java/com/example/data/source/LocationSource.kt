package com.example.data.source

import com.example.data.model.LatLngResponse

interface LocationSource {
    suspend fun getLocation(): LatLngResponse?

    suspend fun getAddress(latitude: Double, longitude : Double, num : Int) : String?

}