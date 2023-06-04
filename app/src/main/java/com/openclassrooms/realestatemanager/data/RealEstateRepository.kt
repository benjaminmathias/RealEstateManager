package com.openclassrooms.realestatemanager.data

import kotlinx.coroutines.flow.Flow

interface RealEstateRepository {

    fun retrieveAndConvertRealEstateEntityList(): Flow<List<RealEstate>>
    fun retrieveAndConvertSpecificRealEstateEntity(id: Long): Flow<RealEstate>

    suspend fun insertRealEstateAndPhoto(realEstate: RealEstate)

    suspend fun setRealEstateAsNoLongerAvailable(saleDate: String, isAvailable: Boolean, id: Long)

    suspend fun editRealEstate(realEstate: RealEstate)
}