package com.openclassrooms.realestatemanager.data


import androidx.room.Database

import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [RealEstateEntity::class, RealEstatePhotoEntity::class], version = 1)
@TypeConverters(PoiTypeConverter::class)
abstract class RealEstateDatabase : RoomDatabase() {

    abstract fun realEstateDao(): RealEstateDao

    abstract fun photoItemDao(): PhotoItemDao
}
