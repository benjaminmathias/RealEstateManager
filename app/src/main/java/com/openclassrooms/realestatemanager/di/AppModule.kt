package com.openclassrooms.realestatemanager.di

import android.content.Context
import androidx.room.Room
import com.openclassrooms.realestatemanager.data.db.RealEstateDatabase
import com.openclassrooms.realestatemanager.data.db.dao.RealEstateDao
import com.openclassrooms.realestatemanager.data.db.dao.RealEstatePhotoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
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

    @Provides
    fun provideIoDispatcher() : CoroutineDispatcher = Dispatchers.IO
}