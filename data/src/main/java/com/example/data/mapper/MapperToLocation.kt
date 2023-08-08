package com.example.data.mapper

import com.example.data.model.LocationResponse
import com.example.domain.model.Location

fun MapperToLocation(latXLngY: LatXLngY) : Location {
    return Location(
        x = latXLngY.x,
        y = latXLngY.y
    )
}