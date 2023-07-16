package com.es.weatherprojects_tuk

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
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
import com.es.weatherprojects_tuk.data.convertGRID_GPS
import com.es.weatherprojects_tuk.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    //위경도 <-> 좌표 변환
    private val TO_GRID = 0
    private val TO_GPS = 1

    // 뷰모델 생성
    private lateinit var weatherViewModel: WeatherViewModel

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

        //현재 날짜 영어로 보여주기
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


        lifecycleScope.launch {
            val locatXY = getLocation()

            val parts = locatXY.address.split(" ")
            Log.d("parts", parts.toString())
            val extracted = parts[2] + " " + parts[3]
            Log.d("extracted", extracted)
            binding.locationTv.text = extracted

            weatherViewModel = ViewModelProvider(this@MainActivity).get(WeatherViewModel::class.java)
            weatherViewModel.getWeather(data_type, num_of_rows, page_no, previousDay, "2300", locatXY.nx, locatXY.ny)
            weatherViewModel.getDayWeather(data_type, num_of_rows, page_no, base_date, previousHourFormatted, locatXY.nx, locatXY.ny)

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
    }

    private suspend fun getLocation(): LatLng {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        val userLocation: Location? = getLatLng()
        val de = LatLng()
        if (userLocation != null) {
            val latitude = userLocation.latitude
            de.lat = latitude
            val longitude = userLocation.longitude
            de.long = longitude
            Log.d("CheckCurrentLocation", "현재 내 위치 값: $latitude, $longitude")

            val mGeoCoder = Geocoder(applicationContext, Locale.KOREAN)
            var mResultList: List<Address>? = null
            try {
                mResultList = mGeoCoder.getFromLocation(latitude, longitude, 1)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (mResultList != null) {
                Log.d("CheckCurrentLocation", mResultList[0].getAddressLine(0))
                de.address = mResultList[0].getAddressLine(0)
            }

            val LatXLngY = convertGRID_GPS(TO_GRID, latitude, longitude)
            Log.d("LATXLNGY", "${LatXLngY.x} and ${LatXLngY.y}")

            de.nx = LatXLngY.x.toInt()
            de.ny = LatXLngY.y.toInt()
        }
        return de
    }

    internal class LatLng {
        var lat = 0.0
        var long = 0.0
        var address = "대한민국 경기도 수원시 팔달구"
        var nx = 0
        var ny = 0
    }

    private fun getLatLng(): Location? {
        val hasFineLocationPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val hasCoarseLocationPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
            val locationProvider = LocationManager.GPS_PROVIDER
            val currentLatLng = locationManager?.getLastKnownLocation(locationProvider)
            if (currentLatLng == null) {
                Toast.makeText(
                    this,
                    "Unable to get location. Please make sure location is enabled",
                    Toast.LENGTH_SHORT
                ).show()
                return null
            }
            return currentLatLng
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    REQUIRED_PERMISSIONS[0]
                )
            ) {
                Toast.makeText(
                    this,
                    "앱을 실행하려면 위치 접근 권한이 필요합니다.",
                    Toast.LENGTH_SHORT
                ).show()
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            }
            return null
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.size == REQUIRED_PERMISSIONS.size) {
            var check_result = true
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false
                    break
                }
            }
            if (check_result) {
                // 권한이 허용된 경우에 대한 처리
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        REQUIRED_PERMISSIONS[1]
                    )
                ) {
                    Toast.makeText(
                        this,
                        "권한 설정이 거부되었습니다.\n앱을 사용하시려면 다시 실행해주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "권한 설정이 거부되었습니다.\n설정에서 권한을 허용해야 합니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
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