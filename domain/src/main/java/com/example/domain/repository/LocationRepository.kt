package com.example.domain.repository


interface LocationRepository {
    suspend fun getLocation(): com.example.domain.model.Location

}