package com.example.data.mapper

import com.example.data.model.WEATHERRESPONSE
import com.example.domain.model.BODY
import com.example.domain.model.HEADER
import com.example.domain.model.ITEM
import com.example.domain.model.ITEMS
import com.example.domain.model.RESPONSE
import com.example.domain.model.Weather

fun MapperToWeather(weatherResponse: WEATHERRESPONSE): Weather {
    return Weather(
        response = RESPONSE(
            header = HEADER(
                resultCode = weatherResponse.response.header.resultCode,
                resultMsg = weatherResponse.response.header.resultMsg
            ),
            body = BODY(
                dataType = weatherResponse.response.body.dataType,
                items = ITEMS(
                    item = weatherResponse.response.body.items.item.map { dataItem ->
                        ITEM(
                            baseDate = dataItem.baseDate,
                            baseTime = dataItem.baseTime,
                            category = dataItem.category,
                            fcstDate = dataItem.fcstDate,
                            fcstTime = dataItem.fcstTime,
                            fcstValue = dataItem.fcstValue,
                            nx = dataItem.nx,
                            ny = dataItem.ny
                        )
                    }
                )
            )
        )
    )
}