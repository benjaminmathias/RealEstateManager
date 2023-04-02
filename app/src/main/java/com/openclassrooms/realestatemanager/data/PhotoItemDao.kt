package com.openclassrooms.realestatemanager.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PhotoItemDao {

    @Insert
    suspend fun insert(photoEntity: PhotoEntity)

    @Query("SELECT * FROM photoItemTable WHERE realEstateId = :id")
    suspend fun getAllSpecificPhotos(id: Long): List<PhotoEntity>
}