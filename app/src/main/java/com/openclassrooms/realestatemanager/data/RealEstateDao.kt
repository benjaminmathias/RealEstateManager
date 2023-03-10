package com.openclassrooms.realestatemanager.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RealEstateDao {

    @Insert
    suspend fun insert(realEstate: RealEstate)

    @Query("SELECT * FROM realEstateTable")
    fun getAllRealEstate(): LiveData<List<RealEstate>>
}