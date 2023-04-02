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

    @Query("SELECT * FROM realEstateTable")
    fun getAllRealEstateFlow(): Flow<List<RealEstateEntity>>

  /*  @Query("SELECT * FROM realEstateTable WHERE id = :id")
    fun getSpecificRealEstate(id: Long): LiveData<RealEstateEntity>*/

    @Query("SELECT * FROM realEstateTable WHERE id = :id")
    fun getSpecificRealEstate(id: Long): Flow<RealEstateEntity>

}