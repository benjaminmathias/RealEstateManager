package com.openclassrooms.realestatemanager.model.data

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

@Dao
interface RealEstateDao {

    @Insert
    suspend fun insert(realEstateEntity: RealEstateEntity): Long

    @Update
    suspend fun update(realEstateEntity: RealEstateEntity)
    

    @Query("UPDATE realEstate SET saleDate = :saleDate, isAvailable = :isAvailable WHERE id LIKE :id")
    suspend fun updateRealEstate(saleDate: OffsetDateTime, isAvailable: Boolean, id: Long)

    @Query("SELECT * FROM realEstate")
    fun getAll(): Flow<List<RealEstateEntity>>

    @Query("SELECT * FROM realEstate WHERE id = :id")
    fun getById(id: Long): Flow<RealEstateEntity>

    @RawQuery(observedEntities = [RealEstateEntity::class])
    fun getAllFiltered(query: SupportSQLiteQuery): List<RealEstateEntity>

    @Query("SELECT * FROM realEstate")
    fun getAllWithCursor() : Cursor
}