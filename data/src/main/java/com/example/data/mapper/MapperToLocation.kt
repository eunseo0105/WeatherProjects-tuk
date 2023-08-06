package com.example.data.mapper

import com.example.data.model.LocationResponse
import com.example.domain.model.Location

fun MapperToLocation(locationResponse: LocationResponse) : Location {
    return Location(
        x = locationResponse.x,
        y = locationResponse.y
    )
}