package com.example.data.source

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.data.model.LatLngResponse
import com.example.data.model.LocationResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationSourceImpl @Inject constructor(@ApplicationContext private val context: Context, private val fusedLocationClient: FusedLocationProviderClient,) :LocationSource {


    override suspend fun getLocation(): LatLngResponse = suspendCancellableCoroutine { continuation ->
        try {
            val locationRequest = LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                smallestDisplacement = 50f
            }

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                continuation.resumeWithException(SecurityException("Location permissions are not granted"))
                return@suspendCancellableCoroutine
            }

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.locations.forEach { location ->
                        Log.d("업데이트 중", "업데이트 중")
                        continuation.resume(LatLngResponse(location.latitude, location.longitude))
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            continuation.invokeOnCancellation {
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }

        } catch (e: Exception) {
            continuation.resumeWithException(e)
        }
    }

    override suspend fun getAddress(latitude: Double, longitude: Double, num: Int): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        val address = addresses?.get(0)?.getAddressLine(0)
        return address
    }
}


