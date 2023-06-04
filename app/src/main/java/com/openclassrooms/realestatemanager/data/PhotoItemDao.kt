package com.openclassrooms.realestatemanager.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PhotoItemDao {

    // TODO : rename to RealEstatePhoto
    @Insert
    suspend fun insert(realEstatePhotoEntity: RealEstatePhotoEntity)

    @Update
    suspend fun update(realEstatePhotoEntity: RealEstatePhotoEntity)

    @Query("DELETE FROM photoItemTable WHERE realEstateId = :realEstateId")
    suspend fun deletePhotosByRealEstateId(realEstateId: Long)

    @Query("SELECT * FROM photoItemTable WHERE realEstateId = :id")
    suspend fun getById(id: Long): List<RealEstatePhotoEntity>
}