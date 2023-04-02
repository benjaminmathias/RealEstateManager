package com.openclassrooms.realestatemanager.data


import androidx.room.Database

import androidx.room.RoomDatabase


@Database(entities = [RealEstateEntity::class, PhotoEntity::class], version = 1)
abstract class RealEstateDatabase : RoomDatabase() {

    abstract fun realEstateDao(): RealEstateDao

    abstract fun photoItemDao(): PhotoItemDao
}
