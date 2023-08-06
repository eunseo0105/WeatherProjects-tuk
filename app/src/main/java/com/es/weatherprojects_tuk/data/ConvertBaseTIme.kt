package com.es.weatherprojects_tuk.data

fun convertBaseTIme(baseTime:String): String {
    var base_time = ""

    if (baseTime.toInt() in 0..159){
        base_time = "2300"
    }
    else if(baseTime.toInt() in 200..459){
        base_time = "0200"
    }
    else if(baseTime.toInt() in 500..759){
        base_time = "0500"
    }
    else if(baseTime.toInt() in 800..1059){
        base_time = "0800"
    }
    else if(baseTime.toInt() in 1100..1359){
        base_time = "1100"
    }
    else if(baseTime.toInt() in 1400..1659){
        base_time = "1400"
    }
    else if(baseTime.toInt() in 1700..1959){
        base_time = "1700"
    }
    else if(baseTime.toInt() in 2000..2259){
        base_time = "2000"
    }
    else if(baseTime.toInt() in 2300..2400){
        base_time = "2300"
    }

    return base_time
}