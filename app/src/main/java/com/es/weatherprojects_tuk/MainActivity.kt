package com.es.weatherprojects_tuk

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.es.weatherprojects_tuk.ViewModel.WeatherViewModel
import com.es.weatherprojects_tuk.databinding.ActivityMainBinding


//지금은 임의로 설정
val num_of_rows = 1000
val page_no = 1
val data_type = "JSON"
val base_time = 1100
val base_data = 20230710
val nx = "55"
val ny = "127"


class MainActivity : AppCompatActivity() {
    // 뷰모델 생성
    private lateinit var weatherviewmodel: WeatherViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        weatherviewmodel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        weatherviewmodel.getWeather(data_type, num_of_rows, page_no, base_data, base_time, nx, ny)

        weatherviewmodel.weatherResponse.observe(this) {it
            Log.d(TAG, "${it.body()}")
            for(i in it.body()?.response!!.body.items.item){
               if (i.category=="TMN" && i.fcstDate=="20230710"){
                   binding.lessMostTempTv.text= "${i.fcstValue}°"
               }
                else if(i.category=="TMX" && i.fcstDate=="20230710"){
                   binding.mostTempTv.text= "${i.fcstValue}°"
               }
            }
            binding.currentTempTv.text = "${it.body()!!.response.body.items.item[0].fcstValue}°"

        }

    }
}