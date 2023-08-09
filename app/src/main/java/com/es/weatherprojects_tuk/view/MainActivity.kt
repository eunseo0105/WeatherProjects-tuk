package com.es.weatherprojects_tuk.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
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
import com.es.weatherprojects_tuk.R
import com.es.weatherprojects_tuk.viewmodel.WeatherViewModel
import com.es.weatherprojects_tuk.adapter.RecyclerViewAdapter
import com.es.weatherprojects_tuk.data.convertBaseTIme
import com.es.weatherprojects_tuk.databinding.ActivityMainBinding
import com.es.weatherprojects_tuk.viewmodel.LocationViewModel
import com.example.domain.model.Weather
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

        //내일 날짜
        calendar.add(Calendar.DAY_OF_MONTH, 1) // 현재 날짜에 하루 추가
        val next_date = dateFormat1.format(calendar.time).toInt()


        //서버로 요청 보낼 데이터 정의
        val num_of_rows = 400
        val page_no = 1
        val data_type = "JSON"
        val base_date = dateFormat1.format(currentTime).toInt()

        // 권한 허용 -> 비동기로 위치 정보 불러오기, 허용 X -> 권한 요청
        if (checkPermissions()) {
            //위치 정보 가져오기
            locationViewModel.fetchLocationAsync()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE)
        }


        locationViewModel.adsLocation.observe(this){
            locationViewModel.getAddress(it.lat, it.lng, 1)

        }
        locationViewModel.address.observe(this){
            val parts = it!!.split(" ")
            Log.d("parts", parts.toString())
            val extracted = parts[2] + " " + parts[3]
            Log.d("extracted", extracted)

            binding.locationTv.text = extracted
        }

        locationViewModel.locationData.observe(this){
            weatherViewModel.getWeather(data_type, num_of_rows, page_no, base_date, "0200", it.x.toInt(), it.y.toInt())
            weatherViewModel.getDayWeather(data_type, num_of_rows, page_no, base_date, previousHourFormatted, it.x.toInt(), it.y.toInt())
        }

        weatherViewModel.weatherResponse.observe(this) { response ->
            Log.d(TAG, "${response.body()}")
            response.body()?.response?.body?.items?.item?.forEach { item ->
                if (item.category == "TMN" && item.fcstDate == base_date.toString()) {
                    Log.d("lessTemp", item.fcstValue)
                    binding.lessMostTempTv.text = "${item.fcstValue}°/"
                } else if (item.category == "TMX" && item.fcstDate == base_date.toString()) {
                    binding.mostTempTv.text = "${item.fcstValue}°"
                }
            }
            weatherViewModel.todayWeather(response.body()!! ,hour, base_date)
            weatherViewModel.tomorrowWeather(response.body()!!, next_date)

        }

        weatherViewModel.dayWeatherResponse.observe(this) { response ->
            Log.d("DAYWEATHER", "${response.body()}")
            response.body()?.response?.body?.items?.item?.forEach { item ->
                if (item.category == "T1H" && item.fcstTime == base_hour) {
                    binding.currentTempTv.text = "${item.fcstValue}°"
                }
            }
        }

        //default - 오늘 날씨
        switchFragment(0)

        //오늘 버튼 클릭
        binding.todayBtn.setOnClickListener {
            Log.d("clicked", "clicked")
            // 현재 버튼 눌림 표시
//            binding.todayBtn.setBackgroundResource(R.color.purple_200) // 예: 눌림 상태의 배경
//
            // 다른 버튼들 눌림 표시 해제
//            binding.tomorrowBtn.setBackgroundResource(R.color.white) // 예: 기본 배경
//            binding.weekBtn.setBackgroundResource(R.color.white)

            switchFragment(0)
        }

        //내일 버튼 클릭
        binding.tomorrowBtn.setOnClickListener {
            switchFragment(1)
        }

        //이번주 버튼 클릭
        binding.weekBtn.setOnClickListener {
            switchFragment(2)
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



    private fun switchFragment(flag: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        when(flag){
            0 -> {
                transaction.replace(R.id.frame_layout, TodayFragment())
            }
            1 -> {
                transaction.replace(R.id.frame_layout, TomorrowFragment())
            }
            2 -> {
                transaction.replace(R.id.frame_layout, TodayFragment())
            }
        }
        transaction.commit()
    }
}