package com.openclassrooms.realestatemanager.utils.location

import com.openclassrooms.realestatemanager.data.model.UserLocation
import kotlinx.coroutines.flow.Flow

interface LocationService {

    val userLocation: Flow<UserLocation>
}