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
import com.example.domain.usecase.LocationUseCase
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LocationViewModel @Inject constructor(@ApplicationContext context: Context, private val locationUseCase: LocationUseCase,private val fusedLocationClient: FusedLocationProviderClient,
) : ViewModel() {

    private val _locationData: MutableLiveData<com.example.domain.model.Location> = MutableLiveData()
    val locationData: LiveData<com.example.domain.model.Location> get() = _locationData

    var _address: MutableLiveData<String> = MutableLiveData()
    val address :LiveData<String> get() = _address

    fun fetchLocationAsync() = viewModelScope.launch {
        val result = locationUseCase.getLocation()
        Log.d("result", "x = ${result.x}, y = ${result.y}")
        _locationData.postValue(result)

    }


}