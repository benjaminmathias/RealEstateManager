package com.openclassrooms.realestatemanager.utils

import android.content.Context
import androidx.room.Room
import com.openclassrooms.realestatemanager.data.RealEstateDao
import com.openclassrooms.realestatemanager.data.RealEstateDatabase
import com.openclassrooms.realestatemanager.data.RealEstatePhotoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideRealEstateDao(realEstateDatabase: RealEstateDatabase): RealEstateDao {
        return realEstateDatabase.realEstateDao()
    }

    @Provides
    fun providePhotoItemDao(realEstateDatabase: RealEstateDatabase): RealEstatePhotoDao {
        return realEstateDatabase.photoItemDao()
    }

    @Provides
    @Singleton
    fun provideRealEstateDatabase(@ApplicationContext appContext: Context):
            RealEstateDatabase {
        return Room.databaseBuilder(
            appContext,
            RealEstateDatabase::class.java,
            "realestate_database"
        )
            .build()
    }
}