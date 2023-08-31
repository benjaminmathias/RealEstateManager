package com.openclassrooms.realestatemanager.model.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RealEstatePhotoDao {

    @Insert
    suspend fun insert(realEstatePhotoEntity: RealEstatePhotoEntity)

    @Update
    suspend fun update(realEstatePhotoEntity: RealEstatePhotoEntity)

    @Query("DELETE FROM realEstatePhoto WHERE realEstateId = :realEstateId")
    suspend fun deletePhotosByRealEstateId(realEstateId: Long)

    @Query("SELECT * FROM realEstatePhoto WHERE realEstateId = :id")
    suspend fun getById(id: Long): List<RealEstatePhotoEntity>
}