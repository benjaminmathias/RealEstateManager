package com.openclassrooms.realestatemanager.data

import androidx.lifecycle.LiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealEstateRepository @Inject constructor(private val realEstateDao: RealEstateDao) {

    val allRealEstates: LiveData<List<RealEstate>> = realEstateDao.getAllRealEstate()

    suspend fun insert(realEstate: RealEstate) {
        realEstateDao.insert(realEstate)
    }
}