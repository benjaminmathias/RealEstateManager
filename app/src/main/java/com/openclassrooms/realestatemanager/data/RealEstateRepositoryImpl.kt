package com.openclassrooms.realestatemanager.data

import kotlinx.coroutines.flow.Flow

interface RealEstateRepositoryImpl {

    fun retrieveAndConvertSpecificRealEstateEntity(id: Long): Flow<RealEstate>

    fun retrieveAndConvertRealEstateEntityList(): Flow<List<RealEstate>>

    suspend fun fetchAllPhotoItem(id: Long): List<PhotoItem>

    suspend fun updateRealEstate(saleDate: String, isAvailable: Boolean, id: Long)

    suspend fun insertRealEstateAndPhoto(realEstate: RealEstate)
}