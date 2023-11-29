package com.openclassrooms.realestatemanager.data.repository

import com.openclassrooms.realestatemanager.data.model.Filters
import com.openclassrooms.realestatemanager.data.model.RealEstate
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

interface RealEstateRepository {

    fun retrieveAndConvertRealEstateEntityList(): Flow<List<RealEstate>>
    fun retrieveAndConvertSpecificRealEstateEntity(id: Long): Flow<RealEstate>

    suspend fun retrieveAndConvertFilteredRealEstateList(filters: Filters): List<RealEstate>

    suspend fun insertRealEstateAndPhoto(realEstate: RealEstate)

    suspend fun setRealEstateAsNoLongerAvailable(saleDate: OffsetDateTime, isAvailable: Boolean, id: Long)

    suspend fun editRealEstate(realEstate: RealEstate)
}