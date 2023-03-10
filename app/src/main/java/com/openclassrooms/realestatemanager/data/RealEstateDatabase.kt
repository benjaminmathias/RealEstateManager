package com.openclassrooms.realestatemanager.data


import androidx.room.Database

import androidx.room.RoomDatabase


@Database(entities = [RealEstate::class], version = 1)
abstract class RealEstateDatabase : RoomDatabase() {

    abstract fun realEstateDao(): RealEstateDao

    /*
    companion object {
        private var instance: RealEstateDatabase? = null

        @Synchronized
        fun getInstance(context: Context): RealEstateDatabase {
            if (instance == null)
                instance = Room.databaseBuilder(
                    context.applicationContext, RealEstateDatabase::class.java,
                    "realestate_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build()

            return instance!!
        }

        private val roomCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                populateDatabase(instance!!)
            }
        }

        private fun populateDatabase(db: RealEstateDatabase) {
            val realEstateDao = db.realEstateDao()
             suspend {
                realEstateDao.insert(RealEstate("Maison 1", 100000, 100))
                realEstateDao.insert(RealEstate("Maison 2", 200000, 200))
                realEstateDao.insert(RealEstate("Maison 3", 300000, 300))
            }
        }

    }
*/

}
