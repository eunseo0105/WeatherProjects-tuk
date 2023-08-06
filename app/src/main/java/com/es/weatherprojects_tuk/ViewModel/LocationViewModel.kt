import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.es.weatherprojects_tuk.data.LocationData
import com.es.weatherprojects_tuk.data.convertGRID_GPS
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.Locale.*
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val _locationData: MutableLiveData<LocationData> = MutableLiveData()
    val locationData: LiveData<LocationData> get() = _locationData

    var _address: MutableLiveData<String> = MutableLiveData()
    val address :LiveData<String> get() = _address

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(application)
    }

    fun fetchLocationAsync() = viewModelScope.launch {
        Log.d("hi","hi")
        try {
            val locationRequest = LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                smallestDisplacement = 50f // 최소 50미터 이동했을 때만 업데이트
            }

            val context = getApplication<Application>().applicationContext
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return@launch
            } else {
                Log.d("hi1","hi1")

                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        locationResult.locations.forEach { location ->
                            Log.d("업데이트 중", "업데이트 중")
                            //위경도
                            Log.d("Location", "Latitude: ${location.latitude}, Longitude: ${location.longitude}")
                            //위경도 ->  x,y 좌표 값으로 변환
                            val XY = convertGRID_GPS(0, location.latitude, location.longitude)
                            Log.d("좌표 값", "${XY.x}, ${XY.y}")
                            //mutablelivedata에 저장
                            _locationData.value = LocationData(XY.x.toInt(), XY.y.toInt())
                            // 현재 주소 받아오기
                            val mGeoCoder = Geocoder(getApplication(), KOREAN)
                            var mResultList: List<Address>? = null
                            try {
                                mResultList = mGeoCoder.getFromLocation(
                                    location.latitude,
                                    location.longitude,
                                    1
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            if (mResultList != null) {
                                Log.d("CheckCurrentLocation", mResultList[0].getAddressLine(0))
                                _address.value = mResultList[0].getAddressLine(0)
                            }
                        }
                    }
                }
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            }
        } catch (e: Exception) {
            Log.e("LocationViewModel", "Error fetching location", e)
        }
    }

    private suspend fun getLastKnownLocation(): Location? = suspendCoroutine { continuation ->
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            numUpdates = 1
        }

        val context = getApplication<Application>().applicationContext
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            continuation.resume(null)
        } else {
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    continuation.resume(p0.lastLocation)
                    fusedLocationClient.removeLocationUpdates(this)
                }
            }
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }
}