package com.es.weatherprojects_tuk.viewmodel

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.es.weatherprojects_tuk.data.LocationData
import com.example.data.mapper.LatXLngY
import com.example.data.mapper.convertGRID_GPS
import com.example.domain.usecase.LocationUseCase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LocationViewModel @Inject constructor(@ApplicationContext context: Context, private val locationUseCase: LocationUseCase,private val fusedLocationClient: FusedLocationProviderClient,
) : ViewModel() {

    //현재 위치 x,y좌표 값
    private val _locationData: MutableLiveData<com.example.domain.model.Location> = MutableLiveData()
    val locationData: LiveData<com.example.domain.model.Location> get() = _locationData

    //현재 위치 위경도
    var _adsLocation: MutableLiveData<LatXLngY> = MutableLiveData()
    val adsLocation :LiveData<LatXLngY> get() = _adsLocation

    //현재 위치
    private val _address : MutableLiveData<String?>  = MutableLiveData()
    val address : LiveData<String?> get() = _address

    fun fetchLocationAsync() = viewModelScope.launch {
        val result = locationUseCase.getLocation()
        Log.d("result", "x = ${result.x}, y = ${result.y}")
        val adsLocation = convertGRID_GPS(1, result.x, result.y)
        Log.d("adsLocation", adsLocation.lat.toString())
        _locationData.postValue(result)
        _adsLocation.postValue(adsLocation)

    }

    fun getAddress(latitude: Double, longitude : Double, num : Int){
        viewModelScope.launch {
            try {
                val result = locationUseCase.getAddress(latitude,longitude, num)
                Log.d("result", result!!)
                _address.postValue(result)

            }catch (e:Exception){
                Log.d("fail address", "${e.message}" )
            }
        }

    }


}