package com.openclassrooms.realestatemanager.model.repo

import com.openclassrooms.realestatemanager.model.data.RealEstate
import kotlinx.coroutines.flow.Flow

interface RealEstateRepository {

    fun retrieveAndConvertRealEstateEntityList(): Flow<List<RealEstate>>
    fun retrieveAndConvertSpecificRealEstateEntity(id: Long): Flow<RealEstate>

    suspend fun retrieveAndConvertFilteredRealEstateList
                (
        type: String?,
        priceMin: String?,
        priceMax: String?,
        surfaceMin: String?,
        surfaceMax: String?,
        room: String?,
        bedroom: String?,
        bathroom: String?,
        location: String?,
        nearbyPOI : List<String>?
    )
    : Flow<List<RealEstate>>

    suspend fun insertRealEstateAndPhoto(realEstate: RealEstate)

    suspend fun setRealEstateAsNoLongerAvailable(saleDate: String, isAvailable: Boolean, id: Long)

    suspend fun editRealEstate(realEstate: RealEstate)
}