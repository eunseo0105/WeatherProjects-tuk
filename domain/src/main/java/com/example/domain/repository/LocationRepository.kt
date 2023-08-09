package com.example.domain.repository


interface LocationRepository {
    suspend fun getLocation(): com.example.domain.model.Location

    suspend fun getAddress(latitude: Double, longitude : Double, num : Int) : String?


}