package com.example.data.source

import com.example.data.model.LatLngResponse

interface LocationSource {
    suspend fun getLocation(): LatLngResponse?

}