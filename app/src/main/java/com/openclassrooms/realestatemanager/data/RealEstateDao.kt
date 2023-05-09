package com.openclassrooms.realestatemanager.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RealEstateDao {

    @Insert
    suspend fun insert(realEstateEntity: RealEstateEntity) : Long

    @Update
    suspend fun update(realEstateEntity: RealEstateEntity)

    @Query("UPDATE realEstateTable SET saleDate = :saleDate, isAvailable = :isAvailable WHERE id LIKE :id")
    suspend fun updateRealEstate(saleDate: String, isAvailable: Boolean, id: Long)
    
    @Query("SELECT * FROM realEstateTable")
    fun getAll(): Flow<List<RealEstateEntity>>

    @Query("SELECT * FROM realEstateTable WHERE id = :id")
    fun getById(id: Long): Flow<RealEstateEntity>

}