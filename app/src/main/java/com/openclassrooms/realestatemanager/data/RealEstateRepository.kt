package com.openclassrooms.realestatemanager.data

import kotlinx.coroutines.flow.Flow

interface RealEstateRepository {

    fun retrieveAndConvertRealEstateEntityList(): Flow<List<RealEstate>>
    fun retrieveAndConvertSpecificRealEstateEntity(id: Long): Flow<RealEstate>

    suspend fun insertRealEstateAndPhoto(realEstate: RealEstate)

    suspend fun updateRealEstate(saleDate: String, isAvailable: Boolean, id: Long)
}