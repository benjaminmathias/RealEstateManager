package com.openclassrooms.realestatemanager.model.data


import androidx.room.Database

import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [RealEstateEntity::class, RealEstatePhotoEntity::class],
    version = 1,
    exportSchema = false)
@TypeConverters(value = [PoiTypeConverter::class, DateTypeConverter::class])
abstract class RealEstateDatabase : RoomDatabase() {

    abstract fun realEstateDao(): RealEstateDao

    abstract fun photoItemDao(): RealEstatePhotoDao
}
