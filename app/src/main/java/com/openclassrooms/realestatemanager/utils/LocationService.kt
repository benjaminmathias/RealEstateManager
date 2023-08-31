package com.openclassrooms.realestatemanager.utils

import com.openclassrooms.realestatemanager.model.data.UserLocation
import kotlinx.coroutines.flow.Flow

interface LocationService {

    val userLocation: Flow<UserLocation>
}