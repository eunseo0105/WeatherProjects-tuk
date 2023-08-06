package com.es.weatherprojects_tuk

import LocationViewModel
import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.es.weatherprojects_tuk.ViewModel.WeatherViewModel
import com.es.weatherprojects_tuk.adapter.RecyclerViewAdapter
import com.es.weatherprojects_tuk.data.WEATHER
import com.es.weatherprojects_tuk.data.convertBaseTIme
import com.es.weatherprojects_tuk.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    // 뷰모델 생성
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var locationViewModel : LocationViewModel

    //권한 설정
    private val PERMISSIONS_REQUEST_CODE = 100
    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        weatherViewModel = ViewModelProvider(this@MainActivity)[WeatherViewModel::class.java]
        locationViewModel = ViewModelProvider(this@MainActivity)[LocationViewModel::class.java]

        //현재 시각, 날짜
        val currentTime: Long = System.currentTimeMillis() // ms로 반환
        val dateFormat1 = SimpleDateFormat("yyyyMMdd") // 년 월 일
        val dateFormat2 = SimpleDateFormat("HHmm") // 시(0~23) 분

        val oneDayInMillis: Long = 24 * 60 * 60 * 1000 // 1일의 밀리초(ms)
        val previousDayTime: Long = currentTime - oneDayInMillis // 현재 시간에서 1일을 뺀 값

        val previousDay = dateFormat1.format(Date(previousDayTime)).toInt() // 최고,최저 기온을 가져오기 위해 현재날짜 - 1일

        //현재 날짜 기준 시간만 추출(분 x)
        val hourFormat = SimpleDateFormat("HH") // 시(0~23)
        val currentHour = hourFormat.format(currentTime)
        val base_hour = "$currentHour" + "00"
        val hour = base_hour.toInt()

        //DayWeather basetime
        val calendar = Calendar.getInstance()
        val currenttime = calendar.timeInMillis

        val currentMinute = calendar.get(Calendar.MINUTE)
        val currenthour = calendar.get(Calendar.HOUR_OF_DAY)

        val previousHour = if (currentMinute >= 45) {
            (currenthour -1).toString()
        } else {
            (currenthour - 1).toString()
        }

        val previousHourParts = "$previousHour:30".split(":")
        val previousHourFormatted = "${previousHourParts[0]}${previousHourParts[1]}"

        //현재 날짜 영어로 보여주기 - UI
        val currentDate = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy/MMMM,dd", Locale.ENGLISH) // 월(MMMM)을 전체 영문 이름으로 표시합니다.
        binding.dateTv.text = formatter.format(currentDate)

        //서버로 요청 보낼 데이터 정의
        val num_of_rows = 400
        val page_no = 1
        val data_type = "JSON"
        val base_time = convertBaseTIme(dateFormat2.format(currentTime))
        val base_date = dateFormat1.format(currentTime).toInt()
        Log.d("dateFormat1", base_date.toString())

        // 권한 허용 -> 비동기로 위치 정보 불러오기, 허용 X -> 권한 요청
        if (checkPermissions()) {
            //위치 정보 가져오는 동안 프로그래스바 실행, 위치 정보 다 가져오면 placeCategory로 intent
            locationViewModel.fetchLocationAsync()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE)
        }

        locationViewModel.address.observe(this){
            val parts = it.split(" ")
            Log.d("parts", parts.toString())
            val extracted = parts[2] + " " + parts[3]
            Log.d("extracted", extracted)

            binding.locationTv.text = extracted
        }

        locationViewModel.locationData.observe(this){
            weatherViewModel.getWeather(data_type, num_of_rows, page_no, previousDay, "2300", it.x, it.y)
            weatherViewModel.getDayWeather(data_type, num_of_rows, page_no, base_date, previousHourFormatted, it.x, it.y)
        }


        weatherViewModel.weatherResponse.observe(this) { response ->
            Log.d(TAG, "${response.body()}")
            response.body()?.response?.body?.items?.item?.forEach { item ->
                if (item.category == "TMN" && item.fcstDate == base_date.toString()) {
                    Log.d("hi", item.fcstValue)
                    binding.lessMostTempTv.text = "${item.fcstValue}°/"
                } else if (item.category == "TMX" && item.fcstDate == base_date.toString()) {
                    binding.mostTempTv.text = "${item.fcstValue}°"
                }
            }

            lifecycleScope.launch {
                fetchAndShowWeather(response.body(),hour, base_date)
            }
        }

        weatherViewModel.dayWeatherResponse.observe(this) { response ->
            Log.d("DAYWEATHER", "${response.body()}")
            response.body()?.response?.body?.items?.item?.forEach { item ->
                if (item.category == "T1H" && item.fcstTime == base_hour) {
                    binding.currentTempTv.text = "${item.fcstValue}°"
                }
            }
        }

        //오늘 버튼 클릭
        binding.todayBtn.setOnClickListener {

        }

        //내일 버튼 클릭
        binding.tomorrowBtn.setOnClickListener {

        }

        //이번주 버튼 클릭
        binding.weekBtn.setOnClickListener {

        }

    }

    // 위치 권한이 허용되었는지 확인 -> true, false
    private fun checkPermissions(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
    // 위치 권한 요청 후 사용자의 응답을 처리하는 메서드
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.size == REQUIRED_PERMISSIONS.size) {
            var checkResult = true
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    checkResult = false
                    break
                }
            }
            // 모든 권한 허용됨 -> 위치 정보 가져오기
            if (checkResult) {
                locationViewModel.fetchLocationAsync()
            }
            // 권한 거부됨 -> 권한이 필요하다는 toast 메세지
            else {
                Toast.makeText(this, "Location permissions are necessary for the app to run", Toast.LENGTH_SHORT).show()
            }
        }
    }



    //위치 변경되면 업데이트
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            // Called when a new location is found by the network location provider.
            val latitude = location.latitude
            val longitude = location.longitude
            Log.d("Updated Location", "Latitude: $latitude, Longitude: $longitude")
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            // Called when the provider status changes.
        }

        override fun onProviderEnabled(provider: String) {
            // Called when the provider is enabled by the user.
        }

        override fun onProviderDisabled(provider: String) {
            // Called when the provider is disabled by the user.
        }
    }


    private suspend fun fetchAndShowWeather(weatherData: WEATHER?,hour:Int, basedate :Int) {

        // 'TMP' 카테고리이고, 현재 시간부터 12시간 동안의 데이터만 필터링
        val filteredData = weatherData?.response?.body?.items?.item?.filter {
            it.category == "TMP" && it.fcstDate== basedate.toString() &&it.fcstTime.toInt() in hour..(hour + 2400)
        }

        Log.d("filteredData", filteredData.toString())

        // RecyclerView 설정
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = filteredData?.let { RecyclerViewAdapter(it) }
    }
}