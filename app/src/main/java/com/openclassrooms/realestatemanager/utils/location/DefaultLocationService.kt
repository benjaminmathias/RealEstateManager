package com.openclassrooms.realestatemanager.utils.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.openclassrooms.realestatemanager.data.model.UserLocation
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultLocationService @Inject constructor(
    @ApplicationContext val context: Context,
) : LocationService {

    private val fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private var geocoder: Geocoder = Geocoder(context)

    private val userPosition: MutableSharedFlow<Pair<Double, Double>> =
        MutableSharedFlow(replay = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val userLocation: Flow<UserLocation> =
        userPosition.flatMapLatest {
            mapCoordinatesToAddress(it.first, it.second)
        }
            .shareIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly, 1)

    init {
        setupLocation()
        geocoder = Geocoder(context, Locale.getDefault())
    }

    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    private fun setupLocation() {
        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task: Task<Location?> ->
            val location = task.result
            if (location != null) {
                try {
                    // Get phone location
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    val latitude = addresses!![0].latitude
                    val longitude = addresses[0].longitude

                    // Set value
                    Log.d("Location", "location is $latitude, $longitude")
                    userPosition.tryEmit(value = Pair(latitude, longitude))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun mapCoordinatesToAddress(latitude: Double, longitude: Double): Flow<UserLocation> {
        val myLocation: MutableSharedFlow<UserLocation> = MutableSharedFlow(replay = 1)

        Geocoder(context, Locale.getDefault())
            .getAddress(latitude, longitude) { address: android.location.Address? ->
                if (address != null) {
                    val addressLine = address.getAddressLine(0).toString()
                    Log.d("Address", addressLine)

                    val location = UserLocation(
                        latitude = latitude,
                        longitude = longitude,
                        address = addressLine
                    )
                    myLocation.tryEmit(location)
                }
            }
        return myLocation
    }

    @Suppress("DEPRECATION")
    private fun Geocoder.getAddress(
        latitude: Double,
        longitude: Double,
        address: (android.location.Address?) -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getFromLocation(latitude, longitude, 1) {
                address(it.firstOrNull())
            }
            return
        }
        try {
            address(getFromLocation(latitude, longitude, 1)?.firstOrNull())
        } catch (e: Exception) {
            // Will catch if there is an internet problem
            address(null)
        }
    }
}