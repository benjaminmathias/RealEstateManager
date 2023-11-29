package com.openclassrooms.realestatemanager.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.openclassrooms.realestatemanager.data.db.dao.DateTypeConverter
import com.openclassrooms.realestatemanager.data.db.dao.PoiTypeConverter
import com.openclassrooms.realestatemanager.data.db.dao.RealEstateDao
import com.openclassrooms.realestatemanager.data.db.dao.RealEstatePhotoDao
import com.openclassrooms.realestatemanager.data.db.entities.RealEstateEntity
import com.openclassrooms.realestatemanager.data.db.entities.RealEstatePhotoEntity

@Database(
    entities = [RealEstateEntity::class, RealEstatePhotoEntity::class],
    version = 1,
    exportSchema = false)
@TypeConverters(value = [PoiTypeConverter::class, DateTypeConverter::class])
abstract class RealEstateDatabase : RoomDatabase() {

    abstract fun realEstateDao(): RealEstateDao

    abstract fun photoItemDao(): RealEstatePhotoDao
}